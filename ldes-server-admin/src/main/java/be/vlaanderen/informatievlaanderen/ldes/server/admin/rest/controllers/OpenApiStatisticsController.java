package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@SuppressWarnings("java:S2479") // whitespace needed for examples
@Tag(name = "Statistics")
public interface OpenApiStatisticsController {

	@Operation(summary = "Retrieve json with statistics of the LDES server")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = @ExampleObject(value = """
					{
					    "ingested member count": 4018,
					    "current member count": 4018,
					    "LDESes": [
					        {
					            "name": "mobility-hindrances",
					            "ingested member count": 4018,
					            "current member count": 4018,
					            "views": [
					                {
					                    "name": "mobility-hindrances/timebased",
					                    "fragmentations": [
					                        {
					                            "name": "HierarchicalTimeBasedFragmentation",
					                            "properties": {
					                                "maxGranularity": "minute",
					                                "fragmentationPath": "http://www.w3.org/ns/prov#generatedAtTime"
					                            }
					                        }
					                    ],
					                    "fragmentation progress": 100
					                }
					            ]
					        }
					    ]
					}
					""")),
	})
	String getStatistics();
}
