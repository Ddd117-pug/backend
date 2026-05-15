package com.toyshop.address.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddressSaveRequest {
    @NotBlank(message = "收货人不能为空")
    private String receiverName;

    @NotBlank(message = "收货手机号不能为空")
    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String detail;

    private String postalCode;

    private Integer isDefault;
}
