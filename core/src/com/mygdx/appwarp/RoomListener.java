package com.mygdx.appwarp;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

public class RoomListener implements RoomRequestListener{

	
	private WarpController callBack;
	
	public RoomListener(WarpController callBack) {
		this.callBack = callBack;
	}
	
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent event) {
//		System.out.println("onGetLiveRoomInfoDone: "+event.getResult());
		callBack.onGetLiveRoomInfo(event);
//		if(event.getResult()==WarpResponseResultCode.SUCCESS){
////			callBack.onGetLiveRoomInfo(event.getJoinedUsers());
//			callBack.onGetLiveRoomInfo(event);
//		}else{
//			callBack.onGetLiveRoomInfo(null);
//		}
	}

	public void onJoinRoomDone(RoomEvent event) {
//		System.out.println("onJoinRoomDone: "+event.getResult());
		if(event.getResult()==WarpResponseResultCode.SUCCESS) {
			callBack.onJoinRoomDone(event.getData().getId());
		} else {
			callBack.onJoinRoomDone(null);
		}
	}

	public void onLeaveRoomDone(RoomEvent arg0) {
		System.out.println("onLeaveRoomDone: "+arg0.getResult());
		callBack.handleLeave();
	}

	public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
		
	}

	public void onSubscribeRoomDone(RoomEvent event) {
//		System.out.println("onSubscribeRoomDone: "+event.getResult());
		if(event.getResult()==WarpResponseResultCode.SUCCESS){
//			callBack.onRoomSubscribed(event.getData().getId());
			callBack.onRoomSubscribed(event);
		}else{
			callBack.onRoomSubscribed(null);
		}
	}

	public void onUnSubscribeRoomDone(RoomEvent arg0) {
		System.out.println("onUnSubscribeRoomDone: "+arg0.getResult());
	}

	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		
	}

	@Override
	public void onLockPropertiesDone (byte result) {
		
	}

	@Override
	public void onUnlockPropertiesDone (byte arg0) {
		
	}

}
