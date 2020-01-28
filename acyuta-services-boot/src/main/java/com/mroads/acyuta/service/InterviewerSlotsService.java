package com.mroads.acyuta.service;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mroads.acyuta.common.ObjectConverter;
import com.mroads.acyuta.dto.InterviewerSlotsDTO;
import com.mroads.acyuta.model.InterviewerSlot;
import com.mroads.acyuta.repository.InterviewerSlotsRepository;

@Service
public class InterviewerSlotsService {

	private static final Logger log = LoggerFactory.getLogger(InterviewerSlotsService.class);

	private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

	@Autowired
	InterviewerSlotsRepository interviewerSlotsRepository;

	
	/**
	 * @param interviewerSlotsDTO
	 * @return
	 */
	public InterviewerSlotsDTO saveInterviewerSlots(InterviewerSlotsDTO interviewerSlotsDTO) {

		InterviewerSlotsDTO slotDto = new InterviewerSlotsDTO();
		try {
			InterviewerSlot slots = new InterviewerSlot();
			slots = interviewerSlotsRepository.save((InterviewerSlot) ObjectConverter.convert(interviewerSlotsDTO, slots));
			slotDto = (InterviewerSlotsDTO) ObjectConverter.convert(slots, slotDto);
		} catch (Exception e) {
			log.error("saveInterviewerSlots : Exception ", e);

		}
		return slotDto;
	}
	
	
	/**
	 * get the InterviewerSlotsDTO based on InterviewerSlotsTable primary key
	 * 
	 * @param interviewerSlotId
	 * @return InterviewerSlotsDTO
	 */
	public InterviewerSlotsDTO getTimeSlotsByInterviewerSlotId(BigInteger interviewerSlotId) {

		InterviewerSlotsDTO resultDto = new InterviewerSlotsDTO();
		try {
			return (InterviewerSlotsDTO) ObjectConverter.convert(interviewerSlotsRepository.findByInterviewerSlotId(interviewerSlotId), resultDto);
		} catch (Exception e) {
			log.error("Exception : getTimeSlotsByInterviewerSlotId ", e);
		}
		return resultDto;
	}
	
	
}
