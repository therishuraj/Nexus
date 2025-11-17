package com.razz.fundingms.command.dto;

public record CreateRequest(
        String title,
        double requiredAmount,
        String funderId,
        String deadline
) {}