package com.sudarshan.rest.command.controllers;

import java.util.Optional;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
public class EventsReplayController {

	@Autowired
	private EventProcessingConfiguration eventProcessingConfiguration;

	@PostMapping("/eventProcessor/{eventProcessName}/reset")
	public ResponseEntity<String> replayEvents(@PathVariable String eventProcessName) {
		Optional<TrackingEventProcessor> trackingEventProcessorOpt = eventProcessingConfiguration.eventProcessor(eventProcessName, TrackingEventProcessor.class);

		if(trackingEventProcessorOpt.isPresent()) {
			TrackingEventProcessor trackingEventProcessor = trackingEventProcessorOpt.get();
			trackingEventProcessor.shutDown();
			trackingEventProcessor.resetTokens();
			trackingEventProcessor.start();

			return ResponseEntity.ok().body("Event Processor with name "+eventProcessName+" has been reset.");
		} else {
			return ResponseEntity.badRequest().body("Event Processor with name "+eventProcessName+" has been reset.");
		}
	}

}
