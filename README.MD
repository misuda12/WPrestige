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
/prestige help                      - Help command obviously ?
/prestige claim                     - (wp.claim) Claims your rewards if present
/prestige set <player> <int:number> - (wp.admin.set) Sets prestige number
/prestige top                       - (wp.top) Shows top islands by block broken ammount
```

## API Usage
```JAVA
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
    List<Prestige> getAll()
    
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
```

Credits: [Mirayi_(admin@warfaremc.eu), misuda12(misuda12@warfaremc.eu), WattMann(wattmann@warfaremc.eu)]

Bugs and Feature requests
=========================
File bug and feature requests here: https://bitbucket.org/warfaremc/warfareprestiges/issues