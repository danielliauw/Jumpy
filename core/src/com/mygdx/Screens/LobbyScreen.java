package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.mygdx.appwarp.WarpController;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;

/**
 * Created by user on 11/3/2016.
 */
public class LobbyScreen extends AbstractScreen {

    private WarpClient warpClient;

    private RoomData room;
    private String roomId;
    private String roomName;
    private String[] liveUsers;

    private final TextButton buttonSend;
    private final TextButton buttonExit;
    private final TextButton buttonChangeAvatar;
    private final TextButton buttonStatusToggle;
    private final TextField textInput;
    private final ScrollPane scrollChat;
    private final Table chatTable;
    private final TextArea textAreaChat;
    private final Label labelRoom;
    private final Label labelNumOfPlayers;
    private final Label labelPlayers;
    private final Label labelStatus;
    private final Label labelAvatar;

    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public LobbyScreen() {
        getWarpClient();

        room = WarpController.getRoom();
        roomId = WarpController.getRoomId();
        roomName = room.getName();

        // start thread to update in-lobby players
        final LobbyUpdateThread lobbyUpdateThread = new LobbyUpdateThread(warpClient,this,roomId);
        lobbyUpdateThread.start();

        buttonSend = new TextButton("Send",skin);
        buttonSend.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                String text = textNewRoom.getText();
                return false;
            }
        });
        buttonExit = new TextButton("Exit",skin);
        buttonExit.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Exit to RoomSel Screen
                // unsubscribe, leave room
                // delete room if empty
                lobbyUpdateThread.interrupt();
                System.out.println("Leaving Room " + roomId + ".");
                warpClient.unsubscribeRoom(roomId);
                warpClient.leaveRoom(roomId);
                ScreenManager.getInstance().showScreen(ScreenEnum.ROOMSELECTION);
//                if (text.length() > 0) {
//                    System.out.println("New Room " + text + " is created.");
//                    warpClient.createRoom(text, WarpController.getLocalUser(), 4, null);
//                }
//                ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY);
                return false;
            }
        });
        buttonChangeAvatar = new TextButton("Change Avatar",skin);
        buttonChangeAvatar.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Switch to Avatar Screen
//                if (text.length() > 0) {
//                    System.out.println("New Room " + text + " is created.");
//                    warpClient.createRoom(text, WarpController.getLocalUser(), 4, null);
//                    ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY);
//                }
                return false;
            }
        });
        buttonStatusToggle = new TextButton("Selecting",skin);
        buttonStatusToggle.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Toggle between Selecting and Ready
//                if (text.length() > 0) {
//                    System.out.println("New Room " + text + " is created.");
//                    warpClient.createRoom(text, WarpController.getLocalUser(), 4, null);
//                    ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY);
//                }
                return false;
            }
        });

        textInput = new TextField("",skin);
        textAreaChat = new TextArea("",skin);
        chatTable = new Table();
        chatTable.add(textAreaChat);
        scrollChat = new ScrollPane(chatTable);
        labelRoom = new Label(roomName,skin);
        warpClient.getLiveRoomInfo(roomId);
        liveUsers = WarpController.getLiveUsers();
        // this is crashing on null
        if (liveUsers != null){
            labelNumOfPlayers = new Label(liveUsers.length + "/" + room.getMaxUsers(),skin);
        } else {
            labelNumOfPlayers = new Label(0 + "/" + room.getMaxUsers(),skin);
        }
        labelPlayers = new Label("Players", skin);
        labelStatus = new Label("Status", skin);
        labelAvatar = new Label("Avatar", skin);

    }

    private void getWarpClient(){
        try {
            warpClient = WarpClient.getInstance();
        } catch (Exception ex) {
            System.out.println("Fail to get warpClient");
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        liveUsers = WarpController.getLiveUsers();
        if (liveUsers!=null && room!=null){
            labelNumOfPlayers.setText(liveUsers.length + "/" + room.getMaxUsers());
        }
    }

    @Override
    public void buildStage() {
        Table tableBig = new Table();
        tableBig.setFillParent(true);
        tableBig.top();
        tableBig.setDebug(true);

        Table tableTop = new Table();
        tableTop.setDebug(true);
        tableTop.add(labelRoom).left().pad(5);
        tableTop.add(labelNumOfPlayers).right().pad(5);
        tableBig.add(tableTop);
        tableBig.row();

        Table tableMid = new Table();
        tableMid.setDebug(true);
        tableMid.add(labelPlayers);
        tableMid.add(labelStatus);
        tableMid.add(labelAvatar);
        tableMid.row();
        tableMid.add(scrollChat).colspan(3);
        tableMid.row();
        tableMid.add(textInput).colspan(2);
        tableMid.add(buttonSend);
        tableMid.row();
        tableMid.add(buttonExit);
        tableMid.add(buttonChangeAvatar);
        tableMid.add(buttonStatusToggle);
        tableBig.add(tableMid);

        addActor(tableBig);
    }
}

class LobbyUpdateThread extends Thread{

    WarpClient warpClient;
    LobbyScreen lobbyScreen;
    String roomId;

    public LobbyUpdateThread(WarpClient warpClient, LobbyScreen lobbyScreen, String roomId) {
        this.warpClient = warpClient;
        this.lobbyScreen = lobbyScreen;
        this.roomId = roomId;
    }

    @Override
    public void run() {
        while (!isInterrupted()){
            warpClient.getLiveRoomInfo(roomId);
            while (!WarpController.isWaitflag()){
                // busy wait
            }
            WarpController.setWaitflag(false);
        }
        System.out.println("thread interrupted");
    }
}
