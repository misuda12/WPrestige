# WPrestige
A BentoBox addon, written by: [Mirayi_, misuda12, WattMann]
Credit for the original idea: [Mirayi_]

![](https://img.shields.io/badge/Compatibility-1.16-green?style=flat-square)
![](https://img.shields.io/badge/Java_version-1.11-blue?style=flat-square)
![](https://img.shields.io/badge/Dependencies-BentoBox,_PlaceholderAPI,_AOneBlock-red?style=flat-square)
## About
Minecraft BentoBox addon written in mixed Kotlin 1.4.10 and Java 1.11. This addon implements multiple features that are sent by AOneBlock addon in order to clarify @WarfareMC top-of-server position

## Commands & Permissions
```  
## Currently availible commands {@since 1.0-SNAPSHOT}
/<prefix> pclaim                     - (wp.claim) Claims your rewards if present
/<prefix> syncp                      - (wp.claim) Will synchronize WPrestige with your actual island
/<prefix> setp <player> <int:number> - (wp.admin.set) Sets prestige number
/<prefix> top                        - (wf.top) Shows top islands by block broken ammount
```

## API Usage
```JAVA
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
```

Credits: [Mirayi_(admin@warfaremc.eu), misuda12(misuda12@warfaremc.eu), WattMann(wattmann@warfaremc.eu)]

Bugs and Feature requests
=========================
File bug and feature requests here: https://bitbucket.org/warfaremc/warfareprestiges/issues