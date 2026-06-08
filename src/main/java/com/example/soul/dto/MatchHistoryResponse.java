package com.example.soul.dto;

import java.time.LocalDateTime;

public class MatchHistoryResponse {

    private Long partnerId;

    private String partnerName;

    private String partnerAvatar;

    private LocalDateTime matchTime;

    public MatchHistoryResponse(
            Long partnerId,
            String partnerName,
            String partnerAvatar,
            LocalDateTime matchTime
    ) {
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.partnerAvatar = partnerAvatar;
        this.matchTime = matchTime;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public String getPartnerAvatar() {
        return partnerAvatar;
    }

    public LocalDateTime getMatchTime() {
        return matchTime;
    }
}
