package com.componente_practico.gacela.modelo;

public class LoginGacela {
	
	private String status;
	private ResultsLogin results;
	
	public LoginGacela(String status, ResultsLogin results) {
		super();
		this.status = status;
		this.results = results;
	}
	
	public LoginGacela() {
		super();
	}

	final public class ResultsLogin{
		private String email;
		private String type;
		private int id;
		private String api_token;
		private String full_name;
		public ResultsLogin(String email, String type, int id, String api_token, String full_name) {
			super();
			this.email = email;
			this.type = type;
			this.id = id;
			this.api_token = api_token;
			this.full_name = full_name;
		}
		public ResultsLogin() {
			super();
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getApi_token() {
			return api_token;
		}
		public void setApi_token(String api_token) {
			this.api_token = api_token;
		}
		public String getFull_name() {
			return full_name;
		}
		public void setFull_name(String full_name) {
			this.full_name = full_name;
		}
		
		
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ResultsLogin getResults() {
		return results;
	}

	public void setResults(ResultsLogin results) {
		this.results = results;
	}

}
