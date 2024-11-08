package com.example.sykkelapp.data.airqualityforecast

data class Variables(
    val AQI: AQI,
    val AQI_no2: AQINo2,
    val AQI_o3: AQIO3,
    val AQI_pm10: AQIPm10,
    val AQI_pm25: AQIPm25,
    val no2_concentration: No2Concentration,
    val no2_local_fraction_heating: No2LocalFractionHeating,
    val no2_local_fraction_industry: No2LocalFractionIndustry,
    val no2_local_fraction_shipping: No2LocalFractionShipping,
    val no2_local_fraction_traffic_exhaust: No2LocalFractionTrafficExhaust,
    val no2_local_fraction_traffic_nonexhaust: No2LocalFractionTrafficNonexhaust,
    val no2_nonlocal_fraction: No2NonlocalFraction,
    val no2_nonlocal_fraction_seasalt: No2NonlocalFractionSeasalt,
    val o3_concentration: O3Concentration,
    val o3_local_fraction_heating: O3LocalFractionHeating,
    val o3_local_fraction_industry: O3LocalFractionIndustry,
    val o3_local_fraction_shipping: O3LocalFractionShipping,
    val o3_local_fraction_traffic_exhaust: O3LocalFractionTrafficExhaust,
    val o3_local_fraction_traffic_nonexhaust: O3LocalFractionTrafficNonexhaust,
    val o3_nonlocal_fraction: O3NonlocalFraction,
    val o3_nonlocal_fraction_seasalt: O3NonlocalFractionSeasalt,
    val pm10_concentration: Pm10Concentration,
    val pm10_local_fraction_heating: Pm10LocalFractionHeating,
    val pm10_local_fraction_industry: Pm10LocalFractionIndustry,
    val pm10_local_fraction_shipping: Pm10LocalFractionShipping,
    val pm10_local_fraction_traffic_exhaust: Pm10LocalFractionTrafficExhaust,
    val pm10_local_fraction_traffic_nonexhaust: Pm10LocalFractionTrafficNonexhaust,
    val pm10_nonlocal_fraction: Pm10NonlocalFraction,
    val pm10_nonlocal_fraction_seasalt: Pm10NonlocalFractionSeasalt,
    val pm25_concentration: Pm25Concentration,
    val pm25_local_fraction_heating: Pm25LocalFractionHeating,
    val pm25_local_fraction_industry: Pm25LocalFractionIndustry,
    val pm25_local_fraction_shipping: Pm25LocalFractionShipping,
    val pm25_local_fraction_traffic_exhaust: Pm25LocalFractionTrafficExhaust,
    val pm25_local_fraction_traffic_nonexhaust: Pm25LocalFractionTrafficNonexhaust,
    val pm25_nonlocal_fraction: Pm25NonlocalFraction,
    val pm25_nonlocal_fraction_seasalt: Pm25NonlocalFractionSeasalt
)