package com.example.tracking.travyotei.tracking_service_travyotei.dto;

import org.springframework.stereotype.Component;

import com.example.tracking.travyotei.tracking_service_travyotei.model.BusLocationStatus;

@Component
public class BusLocationStatusRequestDTOMapper {
    public BusLocationStatus toEntity(BusLocationStatusRequestDTO dto){
        BusLocationStatus loc_stat = new BusLocationStatus();
        loc_stat.setBus_id(dto.getBus_id());
        loc_stat.setCurrent_latitude(dto.getCurrent_latitude());
        loc_stat.setCurrent_longitude(dto.getCurrent_longitude());
        loc_stat.setCurrent_status(dto.getCurrent_status());
        loc_stat.setLast_updated(dto.getLast_updated());


        return loc_stat;
    }


    public BusLocationStatusResponseDTO toResponse(BusLocationStatus loc_stat){

        return new BusLocationStatusResponseDTO(
            loc_stat.getBus_id(),
            loc_stat.getCurrent_latitude(),
            loc_stat.getCurrent_longitude(),
            loc_stat.getCurrent_status(),
            loc_stat.getLast_updated()
        );
    }
}
