package com.ursa.tools.amazon.payload;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Data;

@Data
public class ListResponse {
	private List<?> data;
	private int currentPage;
	private long totalItems;
	private int totalPages;

	public ListResponse(Page<?> pageUsers) {
		 setData(pageUsers.getContent());
	     setCurrentPage(pageUsers.getNumber() + 1);
	     setTotalItems(pageUsers.getTotalElements());
	     setTotalPages(pageUsers.getTotalPages());	
	}
}
