package pl.logic.site.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Message;
import pl.logic.site.model.mysql.Notification;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.mysql.Room;
import pl.logic.site.model.request.SenderRecipientRequest;
import pl.logic.site.model.response.Response;
import pl.logic.site.repository.SpringUserRepository;
import pl.logic.site.service.ChatRoomService;
import pl.logic.site.service.impl.MessageServiceImpl;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:3000")
public class ChatController {


    private final MessageServiceImpl MessageService;
    private final ChatRoomService chatRoomService;

    private final SpringUserRepository springUserRepository;

//    /**
//     * Endpoint used for sending messages to a specific user using WebSocket.
//     * @param Message
//     */
//    @MessageMapping("/sendMessage")
//    public void processMessage(@Payload Message Message) {
//        Message savedMsg = MessageService.save(Message);
//
//        messagingTemplate.convertAndSendToUser(
//                String.valueOf(Message.getRecipientId()), "/queue/messages",
//                new Notification(
//                        savedMsg.getId(),
//                        savedMsg.getSenderId(),
//                        savedMsg.getRecipientId(),
//                        savedMsg.getContent()
//                )
//        );
//    }


    @MessageMapping("/chat.send") // Message mapping for sending messages
    public void sendMessage(@Payload Message message) {
        System.out.println(message);
        MessageService.save(message);
    }

    /**
     * Endpoint used for finding messages by sender and recipient ID
     * (they are user ID of conversing patient and doctor).
     * @param senderId
     * @param recipientId
     * @return List of messages belonging to the specified sender&recipient ID
     */

    @ResponseBody
    @GetMapping("/chatController/messages/{senderId}/{recipientId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully retrieved chat rooms"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "455", description = "Error search process")
    })
    public ResponseEntity<Response> findMessages(@PathVariable int senderId,
                                                 @PathVariable int recipientId) {
        log.info("Wywoływana metoda /messages/senderid/recipientid");
        List<Message> messages;

        try {
            messages = MessageService.findMessages(senderId, recipientId);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", messages));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }

    }



    @ResponseBody
    @GetMapping(value = "/chatController/chats/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves all chats by spring user id from the database", description = "Retrieves all chats by spring user id from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully retrieved chat rooms"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "455", description = "Error search process")
    })
    public ResponseEntity<Response> getChatsBySpringUserId(@Parameter(description = "id of spring user") @PathVariable int id){
        List<Room> rooms = new ArrayList<>();
        try{
            rooms  = MessageService.findAllUserRooms(id);
            return ResponseEntity.status(200).body(new Response<>(Consts.C200, 200, "", rooms));
        } catch (EntityNotFound e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), rooms));
        } catch (DeleteError e) {
            e.printStackTrace();
            return ResponseEntity.status(455).body(new Response<>(e.getMessage(), 455, Arrays.toString(e.getStackTrace()), rooms));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), rooms));
        }
    }

    @ResponseBody
    @PostMapping(value = "/chatController/createChat", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Creates chat for senderId and recipient id", description = "Creates chat for senderId (taken as yours spring user id) and recipient id (taken as recipient user id)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created chat room"),
            @ApiResponse(responseCode = "453", description = "Error during saving an entity")
    })
    public ResponseEntity<Response> getChatsBySpringUserId(@RequestBody SenderRecipientRequest senderRecipientRequest){
        List<Room> rooms = new ArrayList<>();
        try{
            rooms = chatRoomService.createChatId(senderRecipientRequest.getSenderId(), senderRecipientRequest.getRecipientId());
            return ResponseEntity.status(201).body(new Response<>(Consts.C201, 201, "", rooms));
        }  catch (DeleteError e) {
            e.printStackTrace();
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), rooms));
        }  catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), rooms));
        }
    }
}
