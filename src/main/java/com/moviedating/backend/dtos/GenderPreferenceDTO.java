package com.moviedating.backend.dtos;
import com.moviedating.backend.Entity.enums.GenderType;
import lombok.Data;

@Data
public class GenderPreferenceDTO {
    private GenderType genderPreference;
    private GenderType gender;

}
