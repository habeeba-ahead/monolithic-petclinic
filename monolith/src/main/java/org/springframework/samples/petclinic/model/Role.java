/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Enumeration of user roles in the Pet Clinic application.
 * Each role represents different access levels and permissions.
 */
public enum Role implements GrantedAuthority {
    
    /**
     * Administrator role - full access to all application functions
     */
    ADMIN,
    
    /**
     * Veterinarian role - access to medical records and patient management
     */
    VET,
    
    /**
     * Receptionist role - access to scheduling and basic customer service functions
     */
    RECEPTIONIST,
    
    /**
     * Pet owner role - access to own data and pet information only
     */
    OWNER;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}