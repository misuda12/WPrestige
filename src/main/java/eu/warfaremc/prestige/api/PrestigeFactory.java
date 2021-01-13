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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
* The Prestige Factory API used for creating items
*/
public interface PrestigeFactory {

    ItemStack create();

    ItemStack create(@NotNull Material material);
    
    ItemStack create(@NotNull Material material, int ammount);

    ItemStack create(@NotNull Material material, int ammount, @Nullable String ... lore);

    ItemStack create(@NotNull Material material, @Nullable String displayName, int ammount);
    
    ItemStack create(@NotNull Material material, @Nullable String displayName, int ammount, @Nullable String ... lore);

    interface SkullFactory {

        ItemStack createSkull();

        ItemStack createSkull(int ammount);

        ItemStack createSkull(@Nullable UUID uniqueId);

        // TODO

        @Nullable
        ItemStack fromBase64(@NotNull String base64);

        ItemStack fromBase64(@NotNull String base64, @Nullable ItemStack itemStack);

    }
}