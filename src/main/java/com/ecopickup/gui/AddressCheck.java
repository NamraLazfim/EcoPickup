package com.ecopickup.gui;

import com.ecopickup.model.Address;

// Result of checking an address: the address and a message for the user.
record AddressCheck(Address address, String message) {
}
