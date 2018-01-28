package com.catchthing.catchthing.status;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class StateMain {
	private Long userId;
	private Long gameLeftRecord;
	private Long gameRightRecord;
	private HttpStatus status;
}

