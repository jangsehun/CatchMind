package com.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import com.client.Game;


public class ChatServer {

	Game game;
	String answer;

	Vector<ServiceThread> Clients;
	Vector<ServiceThread> Rank;

	// 클라이언트 관리를 벡터로 생성
	public ChatServer() {
		Clients = new Vector<>();
		Rank = new Vector<>();
		game = new Game();
	}

	// 클라이언트 객체 추가
	public void addClient(ServiceThread st) {
		Clients.addElement(st);
		Rank.addElement(st);
	}

	// 클라이언트 객체 삭제
	public void removeClient(ServiceThread st) {
		Clients.removeElement(st);
		Rank.remove(st);
	}

	// 모든 유저에게 메세지 전달
	public void sendMessageAll(String msg) {
		try {
			for (int i = 0; i < Clients.size(); i++) {
				ServiceThread st = ((ServiceThread) Clients.elementAt(i));
				System.out.println(msg);
				st.sendMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessageExAuth(String msg) {
		try {
			for (int i = 0; i < Clients.size(); i++) {
				ServiceThread st = ((ServiceThread) Clients.elementAt(i));
				System.out.println(msg);
				st.sendMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	public void whisper(String toUser, String fromUser, String note) {

		try {
			for (int i = 0; i < Clients.size(); i++) {
				ServiceThread u = (ServiceThread) Clients.elementAt(i);
				if (u.getUserName().equals(toUser)) {
					u.sendMessage("Whisper/" + fromUser + "@" + note);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	public void firstAuthUser() throws IOException {
		if ((Clients.get(0).getUserAuth() == true)) {
			for (int i = 1; i < Clients.size(); i++) {
				Clients.get(i).setUserAuth(false);
				ServiceThread st = ((ServiceThread) Clients.elementAt(i));
				st.sendMessage("unAuth/첫번째 접속자를 제외하고 모든 버튼을 disable함");
			}
		}
	}
	
	/*
	//
	public void checkFirstUser() {
		for (int x = 0; x < Clients.size(); x++) {

		}
	}
	*/
	
	public void randomAuthUser() throws IOException {
		int random = (int) (Math.random() * Clients.size());
		for (int i = 0; i < Clients.size(); i++) {
			Clients.get(i).setUserAuth(false);
		}
		Clients.get(random).setUserAuth(true);
		Clients.get(random).sendMessage("Chatting/>>>>>>>> 당신이 그릴 차례입니다! START를 눌러주세요! <<<<<<<<");
		sendMessageAll("unAuth/권한을 바꾼 다음 버튼을 disable해준다.");
		Clients.get(random).sendMessage("Auth/권한을 부여해준다.");
		System.out.println("new authority number : " + random + "\n new authrized thread : " + Clients.get(random));
	}

	public void userOut(int i) {

		try {
			ServiceThread st = ((ServiceThread) Clients.elementAt(i));
			st.setOutNum(st.getOutNum() + 1);

			sendMessageAll("Chatting/* 주의  * " + st.getUserName() + "님 강퇴표를 받았습니다 (현재 " + st.getOutNum() + "표     "
					+ Math.round(Clients.size()/2.0) + "표 되면 강퇴당합니다)");

			if (st.getOutNum() == Math.round(Clients.size()/2.0)) {

				sendMessageAll("Chatting/" + st.getUserName() + "님이 강퇴 당하셨습니다.");

				removeClient(st);
				st.sendMessage("YouAreOut/ 넌 퇴장이야 ㅋ");
				st.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setUserList() throws IOException {
		for (ServiceThread userList : Clients) { // 각 유저당 한번만 실행되면 됨.
			userList.sendMessage("CleanUserList/Client의 userList창을 초기화 시켜준다.");
			for (int x = 0; x < Clients.size(); x++) { // 각 유저당 여러번 실행되야함(서버가 가지고 잇는 list를 다 보내서 추가해줘야하기때문
				userList.sendMessage("SetUserList/" + Clients.get(x).getUserName());
			}
		}
	}

	public void setRankList() throws IOException {
		Collections.sort(Rank, new RankList());
		for (ServiceThread userList : Rank) { // 각 유저당 한번만 실행되면 됨.
			userList.sendMessage("CleanRankList/Client의 RankList창을 초기화 시켜준다.");

			// test 해볼려고 유저수가 5명이 안되도 출력하게했음 test가 끝나면 게임조건(5명이상일때만 시작된다든지)에 따라 바꿔주면됨
			if (Rank.size() < 5) {
				for (int x = 0; x < Rank.size(); x++) {
					if (Rank.get(x).getScore() != 0) {
						userList.sendMessage("SetRankList/" + Rank.get(x).getUserName() + "  |  "
								+ Rank.get(x).getScore() + " point");
					}
				}
			} else {
				for (int x = 0; x < 5; x++) {
					if (Rank.get(x).getScore() != 0) {
						userList.sendMessage("SetRankList/" + Rank.get(x).getUserName() + "  |  "
								+ Rank.get(x).getScore() + " point");
					}
				}
			}
		}
	}

	// 28일 완성
	public boolean checkUserName(String userName) {
		for (int x = 0; x < Clients.size(); x++) {
			if (userName.equals(Clients.get(x).getUserName())) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		ChatServer server;
		ServerSocket serverSocket = null;

		int port = 9999;
		// 클라이언트를 관리하는 객체(추가, 삭제, 메세지전달)
		server = new ChatServer();

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("연결 실패");
			System.exit(1);
		}
		System.out.println("서버 \n" + serverSocket + "\n에서 연결을 기다립니다.");

		// 유저가 들어올 때 마다 accept한 뒤 thread를 돌려줌
		try {
			while (true) {
				Socket serviceSocket = serverSocket.accept();
				// 클라이언트 관리하는 객체, 클라이언트를 가진 객체를 생성 후 Thread를 통해서 작업을(읽고 쓰는) run 메서드를 명시한 후 실행한다.
				ServiceThread thread = new ServiceThread(server, serviceSocket, true);
				thread.start();
				server.addClient(thread);
			}

		} catch (Exception e) {
			try {
				serverSocket.close();// 서버종료
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}

class RankList implements Comparator<ServiceThread> {
	@Override
	public int compare(ServiceThread first_O, ServiceThread second_O) {

		if (first_O.getScore() < second_O.getScore()) {
			return 1;
		} else if (first_O.getScore() > second_O.getScore()) {
			return -1;
		} else {
			return 0;
		}
	}
}
