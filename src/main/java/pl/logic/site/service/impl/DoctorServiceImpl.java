package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.mysql.*;
import pl.logic.site.model.views.DoctorChat;
import pl.logic.site.model.views.DoctorPatientsFromChats;
import pl.logic.site.repository.*;
import pl.logic.site.service.DoctorService;
import pl.logic.site.model.dao.DoctorDAO;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.exception.EntityNotFound;
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
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private SpecialisationRepository specialisationRepository;
    @Autowired
    private ChartRepository chartRepository;


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
        // Retrieve all Diagnosis Requests for the given doctor
        List<DiagnosisRequest> diagnosisRequests = diagnosisRequestRepository.findAllByIdDoctor(doctorId);

        // Filter diagnosis requests by the date range (from and to)
        List<DiagnosisRequest> filteredRequests = diagnosisRequests.stream()
                .filter(request -> request.getCreationDate() != null && !request.getModificationDate().before(from) && !request.getModificationDate().after(to))
                .collect(Collectors.toList());

        return filteredRequests;
    }
    @Override
    public List<Chart> getMyCharts(final int doctorId, Date from, Date to) {
        // Retrieve all Diagnosis Requests for the given doctor
        List<DiagnosisRequest> diagnosisRequests = diagnosisRequestRepository.findAllByIdDoctor(doctorId);

        // Extract chart IDs from the Diagnosis Requests
        List<Integer> chartIds = new ArrayList<>();
        for (DiagnosisRequest diagnosisRequest : diagnosisRequests) {
            chartIds.add(diagnosisRequest.getIdChart());
        }

        // Retrieve the charts by their IDs
        List<Chart> charts = new ArrayList<>();
        for (Integer chartId : chartIds) {
            chartRepository.findById(chartId).ifPresent(charts::add);
        }

        List<Chart> filteredCharts = new ArrayList<>();
        for (Chart chart : charts) {
            if (chart.getDate() != null && !chart.getDate().before(from) && !chart.getDate().after(to)) {
                filteredCharts.add(chart);
            }
        }

        return filteredCharts;
    }

    @Override
    public List<DoctorChat> getMyChats(final int doctorId) {
        // Fetch the list of DoctorPatientsFromChats entities for the given doctorId
        List<DoctorPatientsFromChats> doctorPatientsFromChatsList = doctorPatientsFromChatsRepository.findAllByDoctorID(doctorId);

        // Filter out null values and collect distinct pairs of doctor and patient IDs
        Set<String> doctorPatientIds = doctorPatientsFromChatsList.stream()
                .filter(Objects::nonNull)
                .map(dpfc -> dpfc.getDoctorID() + "_" + dpfc.getPatientID())
                .collect(Collectors.toSet());

        // Map DoctorPatientsFromChats to DoctorChat objects
        List<DoctorChat> doctorChats = new ArrayList<>();
        for (String idPair : doctorPatientIds) {
            if (idPair.contains("null"))
                continue;

            String[] ids = idPair.split("_");
            int doctorID = Integer.parseInt(ids[0]);
            int patientID = Integer.parseInt(ids[1]);
            DoctorPatientsFromChats doctorPatient = doctorPatientsFromChatsList.stream()
                    .filter(dpfc -> dpfc.getDoctorID() == doctorID && dpfc.getPatientID() == patientID)
                    .findFirst()
                    .orElse(null);
            if (doctorPatient != null) {
                DoctorChat doctorChat = new DoctorChat(
                        doctorPatient.getId(),
                        doctorID,
                        patientID,
                        Math.toIntExact(doctorPatient.getSpringUserID()),
                        new ArrayList<>()
                );
                doctorChats.add(doctorChat);
            }
        }

        // Fetch messages for each chat, filter by date, and populate DoctorChat objects
        for (DoctorChat doctorChat : doctorChats) {
            List<Message> messages = messageRepository.findAllBySenderId(doctorChat.getSpringUserId()).stream()
                    .sorted(Comparator.comparing(Message::getTimestamp))
                    .collect(Collectors.toList());
            doctorChat.setMessages(messages);
        }

        return doctorChats;
    }

    @Override
    public String getMySpecializationName(final int doctorId) {
        return specialisationRepository.findById(doctorId).get().getSpecialisation();
    }

}