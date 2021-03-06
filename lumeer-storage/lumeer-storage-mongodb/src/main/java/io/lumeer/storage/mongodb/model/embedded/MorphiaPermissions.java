/*
 * -----------------------------------------------------------------------\
 * Lumeer
 *  
 * Copyright (C) since 2016 the original author or authors.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -----------------------------------------------------------------------/
 */
package io.lumeer.storage.mongodb.model.embedded;

import io.lumeer.api.model.Permission;
import io.lumeer.api.model.Permissions;

import org.mongodb.morphia.annotations.Embedded;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Embedded
public class MorphiaPermissions implements Permissions {

   public static final String USER_ROLES = "users";
   public static final String GROUP_ROLES = "groups";

   @Embedded(USER_ROLES)
   private Set<MorphiaPermission> userPermissions = new HashSet<>();

   @Embedded(GROUP_ROLES)
   private Set<MorphiaPermission> groupPermissions = new HashSet<>();

   public MorphiaPermissions() {
   }

   public MorphiaPermissions(final Permissions permissions) {
      this.userPermissions = permissions.getUserPermissions().stream()
                                        .map(MorphiaPermission::new)
                                        .collect(Collectors.toSet());
      this.groupPermissions = permissions.getGroupPermissions().stream()
                                         .map(MorphiaPermission::new)
                                         .collect(Collectors.toSet());
   }

   @Override
   public Set<Permission> getUserPermissions() {
      return Collections.unmodifiableSet(userPermissions);
   }

   @Override
   public void updateUserPermissions(final Permission... newUserPermissions) {
      Arrays.stream(newUserPermissions)
            .map(MorphiaPermission::new)
            .forEach(userPermission -> {
               userPermissions.remove(userPermission);
               userPermissions.add(userPermission);
            });
   }

   @Override
   public void removeUserPermission(final String user) {
      userPermissions.removeIf(userRoles -> userRoles.getName().equals(user));
   }

   @Override
   public Set<Permission> getGroupPermissions() {
      return Collections.unmodifiableSet(groupPermissions);
   }

   @Override
   public void updateGroupPermissions(final Permission... newGroupPermissions) {
      Arrays.stream(newGroupPermissions)
            .map(MorphiaPermission::new)
            .forEach(groupPermission -> {
               groupPermissions.remove(groupPermission);
               groupPermissions.add(groupPermission);
            });
   }

   @Override
   public void removeGroupPermission(final String group) {
      groupPermissions.removeIf(groupRoles -> groupRoles.getName().equals(group));
   }

   @Override
   public void clear() {
      userPermissions.clear();
      groupPermissions.clear();
   }

   @Override
   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      }
      if (!(o instanceof Permissions)) {
         return false;
      }

      final Permissions that = (Permissions) o;

      if (getUserPermissions() != null ? !getUserPermissions().equals(that.getUserPermissions()) : that.getUserPermissions() != null) {
         return false;
      }
      return getGroupPermissions() != null ? getGroupPermissions().equals(that.getGroupPermissions()) : that.getGroupPermissions() == null;
   }

   @Override
   public int hashCode() {
      int result = getUserPermissions() != null ? getUserPermissions().hashCode() : 0;
      result = 31 * result + (getGroupPermissions() != null ? getGroupPermissions().hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "MongoPermissions{" +
            "userPermissions=" + userPermissions +
            ", groupPermissions=" + groupPermissions +
            '}';
   }
}
