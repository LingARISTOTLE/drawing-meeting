package com.drawing.drawingmeeting.controller;

import com.drawing.drawingmeeting.server.WebSocketServer;
import com.drawing.drawingmeeting.utils.JsonTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class DrawingController {

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 判断房间是否存在
     * @param roomId
     * @return
     */
    @GetMapping("/isRoomExist/{roomId}")
    public String isRoomExist(@PathVariable("roomId") Integer roomId) {

        boolean exist = WebSocketServer.isRoomExist(roomId);
        HashMap<String, Boolean> result = new HashMap<>();
        result.put("result", exist);
        return JsonTools.getJson(result);

    }

}
