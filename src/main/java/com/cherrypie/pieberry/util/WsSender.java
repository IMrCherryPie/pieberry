package com.cherrypie.pieberry.util;

import com.cherrypie.pieberry.dto.EventType;
import com.cherrypie.pieberry.dto.ObjectType;
import com.cherrypie.pieberry.dto.WsEventDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Data;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Data
@Component
public class WsSender {
    private final SimpMessagingTemplate template;
    private final ObjectMapper mapper;

    public WsSender(SimpMessagingTemplate template, ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    //    Создано замыкание при помощи BiConsumer (Принимает два аргумент и ничего не возвращает)
/*
На момент коды создаётся новый сендер мы определённо знаем какой объект мы хотим отправлять клиенту и
какой у него будет JsonView
    */
    public <T> BiConsumer<EventType, T> getSender(ObjectType objectType, Class view) {
//        настрока мэппера. Устанавливаем JSONView. Так как мы не сохраняем всё в регистре (все мэпперы к   оторые будут возникать с разными JSONView)
//        Выбран путь замыкания, по этому в getSender приходит в Class, котрый мы отправляем мэпперу в качестве аргумента
        ObjectWriter writer = mapper
                .setConfig(mapper.getSerializationConfig())
                .writerWithView(view);
//        Возвращаемая функция. Принимает сообщение и отправляет его в очередь (/topic/activity)
//    Объект будет подвергаться 3 действиям. Создание/удаление/Изменение (для этого создано dto (WsEvenDto))
        return (EventType eventType, T payload) -> {
            String value = null;

            try {
                value = writer.writeValueAsString(payload);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            template.convertAndSend(
                    "/topic/activity",
                    new WsEventDto(objectType, eventType, value)
            );
        };
    }
}
