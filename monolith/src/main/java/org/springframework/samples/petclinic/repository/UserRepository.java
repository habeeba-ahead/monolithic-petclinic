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
package org.springframework.samples.petclinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.model.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository class for <code>User</code> domain objects for authentication.
 * All method names are compliant with Spring Data naming conventions so this 
 * interface can easily be extended for Spring Data.
 *
 * @author Spring PetClinic Team
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Retrieve a {@link User} from the data store by username.
     * @param username the username to search for
     * @return the {@link User} if found, null otherwise
     */
    @Query("SELECT user FROM User user WHERE user.username = :username")
    @Transactional(readOnly = true)
    User findByUsername(@Param("username") String username);

    /**
     * Check if a user exists with the given username.
     * @param username the username to check
     * @return true if a user exists with the given username, false otherwise
     */
    boolean existsByUsername(String username);
}