/*
 * This file is part of WarfareMC, licensed under the MIT License.
 *
 * Copyright (C) 2020 WarfareMC & Team
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.warfaremc.prestige.api;

import eu.warfaremc.prestige.model.Prestige;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static eu.warfaremc.prestige.PrestigeAddonKt.api;

/**
 * The Prestige API is used handle some unsafes of WPrestige
 */
public interface PrestigeAPI {

    /**
     * Adds player to WPRestige system, which is based on UUIDs
     * <pre>
     * var uuid = Player#getUniqueId
     * (void) PrestigeAPI#addPrestige(uniqueId)
     * </pre>
     *
     * @param uniqueId the UUID of player we want to add
     * @return Current prestige level
     */
    int addPrestige(@Nullable UUID uniqueId);

    /**
     * Sets a player progression
     * <pre>
     * var uuid = Player#getUniqueId
     * int number = any#number .. range 0-9999
     * (void) PrestigeAPI#setPrestige(uniqueId, number)
     *
     * To save data we can use {@link #setPrestige(UUID, int, boolean)}
     *                         {@link #save(UUID)}
     * </pre>
     *
     * @param uniqueId the UUID of player we want to set
     * @param number number that represents prestige progress
     */
    default void setPrestige(@Nullable UUID uniqueId, int number) {
        setPrestige(uniqueId, number, true);
    }

    /**
     * Sets a player progression
     * <pre>
     * var uuid = Player#getUniqueId
     * int number = any#number .. range 0-9999
     * (void) PrestigeAPI#setPrestige(uniqueId, number)
     * </pre>
     *
     * @param uniqueId the UUID of player we want to set
     * @param number number that represents prestige progress
     * @param save if we want to also save this data
     */
    void setPrestige(@Nullable UUID uniqueId, int number, boolean save);

    /**
     * Gets player progression number
     * <pre>
     * var uuid = Player#getUniqueId
     * (int) PrestigeAPI#getPrestige(uniqueId)
     * </pre>
     *
     * @param uniqueId the UUID of player we want to get from
     * @return a number that represents prestige progress
     */
    int getPrestige(@Nullable UUID uniqueId);

    /**
     * Get all currently saved prestiges
     * <pre>
     * (List) var list = PrestigeAPI#getAll
     *
     * </pre>
     *
     * @return List with Prestige class {@link Prestige}
     */
    List<Prestige> getAll();


    /**
     * Gets player progression number
     * <pre>
     * var uuid = Player#getUniqueId
     * (int) PrestigeAPI#getCurrectPrestige(uniqueId)
     * </pre>
     *
     * @param uniqueId the UUID of player we want to get from
     * @return a number that represents prestige progress
     * @deprecated replaced by {@link #getPrestige(UUID)}
     */

    @Deprecated
    default int getCurrectPrestige(@Nullable UUID uniqueId) {
        return getPrestige(uniqueId);
    }

    /**
     * Checks if player uuid exits in WPrestige system
     * <pre>
     * var uuid = Player#getUniqueId
     * (boolean) PrestigeAPI#exists(uniqueId)
     * </pre>
     *
     * @param uniqueId the UUID of player we want to check
     * @return a boolean if player exists in WPrestige
     */
    boolean exists(@Nullable UUID uniqueId);

    /**
     * Checks if player uuid exits in WPrestige system
     * and removes it, or returns null if not exists
     *
     * @param uniqueId the UUID of player we want to remove
     * @return a {@link Prestige} if player exists in WPrestige
     *         or returns null
     */

    @Nullable
    Prestige remove(@Nullable UUID uniqueId);

    /**
     * This method is used for saving data
     * <pre>
     * var uuid = Player#getUniqueId
     *            PrestigeAPI#save(uniqueId)
     * </pre>
     *
     * @param uniqueId the UUID of player we want to save from
     */
    void save(@Nullable UUID uniqueId);

    /**
     * This method is used for saving data in bulk
     * @param bulk Map that contains uuids of players
     *             and their corresponding data you want
     *             to save
     */
    void save(@NotNull Map<UUID, Integer> bulk);

    /**
     * Bake API instance
     * <pre>
     * Java:
     * (PrestigeAPI) var api = PrestigeAPI.getInstance()
     *                   api#method()
     * Kotlin:
     * (PrestigeAPI) var api = API
     *                   api#method()
     * </pre>
     *
     * @return API instance
     */

    @NotNull
    static PrestigeAPI getInstance() {
        return api;
    }
}