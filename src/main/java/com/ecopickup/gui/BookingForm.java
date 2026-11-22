package com.ecopickup.gui;

import com.ecopickup.model.MaterialType;

// What the resident typed on the booking page, before it is checked.
record BookingForm(String address, MaterialType material, String dateText, String weightText,
                          String notes, String access) {
}
