package com.luigi.lifeos.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class RecurringPeriod { DAILY, WEEKLY, MONTHLY, YEARLY }
