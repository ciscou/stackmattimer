package com.rubikaz.cisco.tuenti;

public class AsynchronousTuenti {
	public AsynchronousTuenti() {
		tuenti = new Tuenti();
	}

	public AsynchronousTuenti(int user_id, String session_id) {
		tuenti = new Tuenti(user_id, session_id);
	}

	public void login(String email, String password,
			TuentiRequestListener listener) {
		try {
			tuenti.login(email, password);
			listener.onComplete();
		} catch (TuentiException te) {
			listener.onError(te);
		}
	}

	public void addPostToProfileWall(int user_id, String body,
			TuentiRequestListener listener) {
		try {
			tuenti.addPostToProfileWall(user_id, body);
			listener.onComplete();
		} catch (TuentiException te) {
			listener.onError(te);
		}
	}

	public boolean isAuthenticated() {
		return tuenti.isAuthenticated();
	}

	public int getUserID() {
		return tuenti.getUserID();
	}

	public String getSessionID() {
		return tuenti.getSessionID();
	}

	private Tuenti tuenti;
}
