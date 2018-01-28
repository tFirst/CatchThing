package com.catchthing.catchthing.status;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StateMain {
	private Long userId;
	private Long gameLeftRecord;
	private Long gameRightRecord;
	private HttpStatus status;

//	public StateMain() {
//	}
//
//	public void setUserId(Long userId) {
//		this.userId = userId;
//	}
//
//	public void setGameLeftRecord(Long gameLeftRecord) {
//		this.gameLeftRecord = gameLeftRecord;
//	}
//
//	public void setGameRightRecord(Long gameRightRecord) {
//		this.gameRightRecord = gameRightRecord;
//	}
//
//	public void setStatus(HttpStatus status) {
//		this.status = status;
//	}
//
//	public Long getUserId() {
//		return userId;
//	}
//
//	public Long getGameLeftRecord() {
//		return gameLeftRecord;
//	}
//
//	public Long getGameRightRecord() {
//		return gameRightRecord;
//	}
//
//	public HttpStatus getStatus() {
//		return status;
//	}
//
//	@Override
//	public String toString() {
//		return "StateMain{" +
//				"userId=" + userId +
//				", gameLeftRecord=" + gameLeftRecord +
//				", gameRightRecord=" + gameRightRecord +
//				", status=" + status +
//				'}';
//	}
}

