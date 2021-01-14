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

import static eu.warfaremc.prestige.PrestigeAddonKt.api;

public interface PrestigeAPI {

    /**
     * Adds player to WPRestige system, which is based on BentoBoxUUID
     * <pre>
     * var uuid = Island#getUniqueId
     * (int) PrestigeAPI#addPrestige(uniqueId)
     * </pre>
     *
     * @param uniqueId the UUID of island we want to add to
     * @return Current prestige level + 1
     */
    int addPrestige(@Nullable String uniqueId);

    /**
     * Sets a island progression
     * This will autosave progression
     * <pre>
     * var uuid = Island#getUniqueId
     * int number = any#number range 0 .. 10++
     * (void) PrestigeAPI#setPrestige(uniqueId, number)
     *
     * To save data we can also use {@link #save(String)}
     * </pre>
     *
     * @param uniqueId the UUID of island we want to set
     * @param number number that represents prestige progress
     */
    void setPrestige(@Nullable String uniqueId, int number);

    /**
     * Gets player progression number
     * <pre>
     * var uuid = Island#getUniqueId
     * (int) PrestigeAPI#getPrestige(uniqueId)
     * </pre>
     *
     * @param uniqueId the UUID of player we want to get from
     * @return a number that represents prestige progress
     */
    int getPrestige(@Nullable String uniqueId);

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
     * var uuid = Island#getUniqueId
     * (int) PrestigeAPI#getCurrectPrestige(uniqueId)
     * </pre>
     *
     * @param uniqueId the UUID of island we want to get from
     * @return a number that represents prestige progress
     * @deprecated replaced by {@link #getPrestige(String)}
     */
    @Deprecated
    int getCurrentPrestige(@Nullable String uniqueId);

    /**
     * Checks if player uuid exits in WPrestige system
     * <pre>
     * var uuid = Island#getUniqueId
     * (boolean) PrestigeAPI#exists(uniqueId)
     * </pre>
     *
     * @param uniqueId the UUID of island we want to check
     * @return a boolean if player exists in WPrestige
     */
    boolean exists(@Nullable String uniqueId);

    /**
     * Checks if island uuid exits in WPrestige system
     * and removes it, or returns null if not exists
     *
     * @param uniqueId the UUID of island we want to remove
     * @return a {@link Prestige} if islandID exists in WPrestige
     *         or returns null
     */

    @Nullable
    Prestige remove(@Nullable String uniqueId);

    /**
     * This method is used for saving data
     * <pre>
     * var uuid = Island#getUniqueId
     *            PrestigeAPI#save(uniqueId)
     * </pre>
     *
     * @param uniqueId the UUID of island we want to save from
     */
    void save(@Nullable String uniqueId);

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