package com.tfgkk.farmer_platform.dto.platform;

import jakarta.validation.constraints.NotBlank;

public class ShipOrderRequest {
    @NotBlank(message = "Ship company is required")
    private String shipCompany;
    @NotBlank(message = "Ship number is required")
    private String shipNo;

    public String getShipCompany() { return shipCompany; }
    public void setShipCompany(String shipCompany) { this.shipCompany = shipCompany; }
    public String getShipNo() { return shipNo; }
    public void setShipNo(String shipNo) { this.shipNo = shipNo; }
}
