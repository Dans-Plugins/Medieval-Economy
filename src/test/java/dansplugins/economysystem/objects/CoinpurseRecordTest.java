package dansplugins.economysystem.objects;

import org.junit.Test;

import java.util.Scanner;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the parsing behind {@link Coinpurse#load(String)}, reached through readRecord so that
 * no record file has to be written. A record that cannot be parsed must leave the coinpurse without
 * a UUID, which is what StorageService.loadCoinpurses skips on, rather than throw out of onEnable.
 */
public class CoinpurseRecordTest {

    private static final UUID PLAYER = UUID.fromString("0f6c1a52-3d0e-4a5b-9c1e-7b2f4d8e6a90");

    private Coinpurse read(String record, boolean expectedResult) {
        Coinpurse purse = new Coinpurse(null);
        assertEquals(expectedResult, purse.readRecord(new Scanner(record)));
        return purse;
    }

    @Test
    public void readRecord_asWrittenBySave_restoresUuidAndBalance() {
        Coinpurse purse = read(PLAYER + "\n" + 137 + "\n", true);

        assertEquals(PLAYER, purse.getPlayerUUID());
        assertEquals(137, purse.getCoins());
    }

    @Test
    public void readRecord_withNoBalanceLine_keepsUuidWithZeroCoins() {
        Coinpurse purse = read(PLAYER + "\n", true);

        assertEquals(PLAYER, purse.getPlayerUUID());
        assertEquals(0, purse.getCoins());
    }

    @Test
    public void readRecord_withMalformedUuid_leavesPurseUnidentified() {
        Coinpurse purse = read("not-a-uuid\n137\n", false);

        assertNull(purse.getPlayerUUID());
        assertEquals(0, purse.getCoins());
    }

    @Test
    public void readRecord_withMalformedBalance_leavesPurseUnidentified() {
        // the UUID line parses, so it has to be discarded again for the loader to skip the record
        Coinpurse purse = read(PLAYER + "\n137 coins\n", false);

        assertNull(purse.getPlayerUUID());
        assertEquals(0, purse.getCoins());
    }

    @Test
    public void readRecord_withEmptyRecord_leavesPurseUnidentified() {
        Coinpurse purse = read("", true);

        assertNull(purse.getPlayerUUID());
        assertTrue(purse.containsAtLeast(0));
        assertFalse(purse.containsAtLeast(1));
    }
}
