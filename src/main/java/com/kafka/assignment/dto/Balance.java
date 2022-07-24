package com.kafka.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@With
public class Balance {
	
	private String balanceId;
    private String accountId;
    private float balance;
    
}
