package dansplugins.economysystem.objects;

import org.junit.Test;

import java.util.Scanner;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for the parsing behind {@link Coinpurse#legacyLoad(String)}, reached through
 * readLegacyRecord so that no record file has to be written and no server has to resolve the
 * player name. A pre-v0.7 record whose balance cannot be parsed must leave the coinpurse without a
 * UUID, which is what StorageService.legacyLoadCoinpurses skips on, rather than throw out of
 * onEnable.
 */
public class LegacyCoinpurseRecordTest {

    private static final UUID PLAYER = UUID.fromString("3b1d7e64-58a2-4c0f-8e9d-2a6f1c4b7d35");

    private static final Function<String, UUID> KNOWS_ALICE = name -> "Alice".equals(name) ? PLAYER : null;

    private Coinpurse read(String record, boolean expectedResult) {
        Coinpurse purse = new Coinpurse(null);
        assertEquals(expectedResult, purse.readLegacyRecord(new Scanner(record), KNOWS_ALICE));
        return purse;
    }

    @Test
    public void readLegacyRecord_withAKnownName_restoresUuidAndBalance() {
        Coinpurse purse = read("Alice\n137\n", true);

        assertEquals(PLAYER, purse.getPlayerUUID());
        assertEquals(137, purse.getCoins());
    }

    /**
     * The balance is kept, but with no UUID the coinpurse is skipped by the loader all the same:
     * the coins cannot be attributed to anyone.
     */
    @Test
    public void readLegacyRecord_withAnUnknownName_leavesPurseUnidentified() {
        Coinpurse purse = read("Bob\n137\n", true);

        assertNull(purse.getPlayerUUID());
    }

    @Test
    public void readLegacyRecord_withMalformedBalance_leavesPurseUnidentified() {
        // the name resolves, so the UUID has to be discarded again for the loader to skip the record
        Coinpurse purse = read("Alice\n137 coins\n", false);

        assertNull(purse.getPlayerUUID());
        assertEquals(0, purse.getCoins());
    }

    @Test
    public void readLegacyRecord_withNoBalanceLine_keepsUuidWithZeroCoins() {
        Coinpurse purse = read("Alice\n", true);

        assertEquals(PLAYER, purse.getPlayerUUID());
        assertEquals(0, purse.getCoins());
    }
}
