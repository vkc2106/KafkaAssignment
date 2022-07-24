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
public class CustomerBalance {

	private String accountId;
    private String customerId;
    private String phone;
    private float balance;
    
}
