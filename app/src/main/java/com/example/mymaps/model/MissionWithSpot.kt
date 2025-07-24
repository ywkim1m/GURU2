package com.example.mymaps.model

import androidx.room.Embedded
import androidx.room.Relation

data class MissionWithSpot(
    @Embedded val mission: MissionEntity,
    @Relation(
        parentColumn = "spotId",
        entityColumn = "id"
    )
    val spot: SpotEntity?
)
