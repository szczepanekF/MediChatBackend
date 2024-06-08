package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.mysql.Chart;
import pl.logic.site.model.mysql.DiagnosisRequest;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.views.DoctorPatientsFromChats;
import pl.logic.site.repository.DiagnosisRequestRepository;
import pl.logic.site.repository.DoctorPatientsFromChatsRepository;
import pl.logic.site.repository.PatientRepository;
import pl.logic.site.service.DoctorService;
import pl.logic.site.model.dao.DoctorDAO;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Doctor;
import pl.logic.site.repository.DoctorRepository;
import pl.logic.site.utils.Consts;
import pl.logic.site.model.views.DoctorPatientsWithData;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private DiagnosisRequestRepository diagnosisRequestRepository;
    @Autowired
    private DoctorPatientsFromChatsRepository doctorPatientsFromChatsRepository;
    @Autowired
    private PatientRepository patientRepository;


    @Override
    @Transactional
    public Doctor createDoctor(final DoctorDAO doctor) {
        Doctor doctorEntity = new Doctor(doctor.doctor().getId(),
                doctor.doctor().getName(),
                doctor.doctor().getSurname(),
                doctor.doctor().getBirth_date(),
                doctor.doctor().getSpecialisation_id(),
                doctor.doctor().getIsBot());
        if (doctorEntity.getId() != 0)
            throw new SaveError(Consts.C453_SAVING_ERROR + " Explicitly stated entity ID, entity: " + doctorEntity);



        Doctor returned;
        try {
            returned = doctorRepository.saveAndFlush(doctorEntity);
        } catch (Exception e) {
            throw new SaveError(Consts.C453_SAVING_ERROR + " " + doctorEntity);
        }
        return returned;
    }

    @Override
    @Transactional
    public void deleteDoctor(int doctorId) {
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        if (doctor.isEmpty())
            throw new EntityNotFound(Consts.C404 + " ID: " + doctorId + " Type: " + this.getClass());

        try {
            doctorRepository.deleteById(doctor.get().getId());
        } catch (Exception e) {
            throw new DeleteError(Consts.C455_DELETING_ERROR + " " + doctor);
        }
    }

    @Override
    @Transactional
    public Doctor updateDoctor(final DoctorDAO doctor, int doctorId) {
        Doctor doctorEntity = new Doctor(doctor.doctor().getId(),
                doctor.doctor().getName(),
                doctor.doctor().getSurname(),
                doctor.doctor().getBirth_date(),
                doctor.doctor().getSpecialisation_id(),
                doctor.doctor().getIsBot());
        doctorEntity.setId(doctorId);

        Optional<Doctor> doctorFromDatabse = doctorRepository.findById(doctorId);
        if (doctorFromDatabse.isEmpty())
            throw new EntityNotFound(Consts.C404 + " " + doctorEntity);

        Doctor returned;
        try {
            returned = doctorRepository.saveAndFlush(doctorEntity);
        } catch (Exception e) {
            throw new SaveError(Consts.C454_UPDATING_ERROR + " " + doctorEntity);
        }
        return returned;
    }

    @Override
    public Doctor getDoctor(final int doctorId) {
        return doctorRepository.findById(doctorId).orElseThrow(() -> new EntityNotFound(Consts.C404 + " ID: " + doctorId + " Type: " + this.getClass()));
    }

    @Override
    public List<Doctor> getDoctors(int doctorFilter) {
        List<Doctor> doctors;
        if (doctorFilter == 2)
            doctors = doctorRepository.findAll();
        else
            doctors = doctorRepository.retrieveDoctorsByType(doctorFilter);
        if (doctors.isEmpty())
            throw new EntityNotFound(Consts.C404);
        return doctors;
    }

    @Override
    public Doctor getDoctorByDiagnosisRequest(int diagnosisRequestId) {
        return doctorRepository.findAllById(diagnosisRequestRepository.findById(diagnosisRequestId).get().getIdDoctor());

    }

    @Override
    public List<Patient> getMyPatients(final int doctorId) {
        try {

            List<DoctorPatientsFromChats> doctorPatientsFromChatsList = doctorPatientsFromChatsRepository.findAllByDoctorID(doctorId);

            List<Long> patientIds = doctorPatientsFromChatsList.stream()
                    .filter(Objects::nonNull) // Ensure no null values in the list
                    .map(DoctorPatientsFromChats::getPatientID)
                    .filter(Objects::nonNull) // Ensure no null patient IDs
                    .collect(Collectors.toList());

            List<Integer> patientIdsAsIntegers = patientIds.stream()
                    .map(Long::intValue)
                    .collect(Collectors.toList());

            List<Patient> patients = patientRepository.findAllById(patientIdsAsIntegers);
            return patients;
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<DoctorPatientsWithData> getMyPatientsByDate(final int doctorId, Date fromDate, Date toDate) {
        // Fetch the list of DoctorPatientsFromChats entities for the given doctorId
        List<DoctorPatientsFromChats> doctorPatientsFromChatsList = doctorPatientsFromChatsRepository.findAllByDoctorID(doctorId);

        // Filter records where dateofcontact is between fromDate and toDate
        List<DoctorPatientsFromChats> filteredList = doctorPatientsFromChatsList.stream()
                .filter(dpfc -> dpfc.getDateofcontact() != null && !dpfc.getDateofcontact().before(fromDate) && !dpfc.getDateofcontact().after(toDate))
                .filter(dpfc -> dpfc.getPatientID() != null)
                .filter(dpfc -> dpfc.getIsDiagnosisRequest() != null)
                .collect(Collectors.toList());

        // Group DoctorPatientsFromChats by patientId and find the one with the earliest dateofcontact for each group
        Map<Long, Optional<DoctorPatientsFromChats>> earliestContactMap = filteredList.stream()
                .collect(Collectors.groupingBy(DoctorPatientsFromChats::getPatientID,
                        Collectors.minBy(Comparator.comparing(DoctorPatientsFromChats::getDateofcontact))));

        // Fetch Patient entities from the repository using the patient IDs
        List<Long> patientIds = filteredList.stream()
                .map(DoctorPatientsFromChats::getPatientID)
                .distinct()
                .collect(Collectors.toList());

        List<Integer> patientIdsAsIntegers = patientIds.stream()
                .map(Long::intValue)
                .collect(Collectors.toList());

        List<Patient> patients = patientRepository.findAllById(patientIdsAsIntegers);

        Map<Integer, Patient> patientMap = patients.stream()
                .collect(Collectors.toMap(Patient::getId, patient -> patient));

        // Create a list of DoctorPatientsWithData objects for the earliest contacts
        List<DoctorPatientsWithData> doctorPatientsWithDataList = earliestContactMap.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(dpfc -> new DoctorPatientsWithData(
                        patientMap.get(dpfc.getPatientID().intValue()),
                        dpfc.getIsDiagnosisRequest(),
                        dpfc.getDateofcontact()))
                .collect(Collectors.toList());

        return doctorPatientsWithDataList;
    }

    @Override
    public List<DiagnosisRequest> getMyDiagnosisRequests(int doctorId, Date from, Date to) {
        return List.of();
    }

    @Override
    public List<Chart> getMyCharts(int doctorId, Date from, Date to) {
        return List.of();
    }
}