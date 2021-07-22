package com.bdca.sense.controller;

public class ResponseMessage {
	private String error;
	private String msg;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public ResponseMessage(String error) {
		this.error = error;
	}

	public ResponseMessage(String error, String msg) {
		this.error = error;
		this.msg = msg;
	}

}
