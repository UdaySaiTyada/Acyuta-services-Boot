package com.mroads.acyuta.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class DashboardDTO {
    public String startDate;
    public String endDate;
    public Integer jobOpenings;
    public Long resumesAdded;
    public Long clientSubmissions;
    public Integer closures;
    public List<RecruitersStatsDTO> activeRecruiters;
    public List<RecruitersStatsDTO> inActiveRecruiters;
}