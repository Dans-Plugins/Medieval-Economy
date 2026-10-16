package dansplugins.economysystem.services;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Pins the recursive delete UtilityService.ensureSmoothTransitionBetweenVersions() runs on the
 * pre-v0.7 save folder, ./plugins/Medieval-Economy/. It removes everything beneath the folder it
 * is given except a file whose absolute path contains "config.yml", which is kept so the server
 * owner's settings survive the migration.
 *
 * The method reads nothing through the plugin, so null stands in for it, the way
 * CoinpurseLookupTest constructs Coinpurse.
 */
public class LegacyFilesDeletionTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final UtilityService utilityService = new UtilityService(null);

    private File createFile(File parent, String name) throws IOException {
        File file = new File(parent, name);
        assertTrue(file.createNewFile());
        return file;
    }

    @Test
    public void aFolderOfRecords_isDeletedWithEverythingInIt() throws Exception {
        File legacyFolder = temporaryFolder.newFolder("Medieval-Economy");
        createFile(legacyFolder, "coinpurse-record-filenames.txt");
        createFile(legacyFolder, "Steve.txt");
        File nested = new File(legacyFolder, "nested");
        assertTrue(nested.mkdir());
        createFile(nested, "Alex.txt");

        assertTrue(utilityService.deleteLegacyFiles(legacyFolder));
        assertFalse(legacyFolder.exists());
    }

    @Test
    public void aSingleFile_isDeleted() throws Exception {
        File record = createFile(temporaryFolder.getRoot(), "Steve.txt");

        assertTrue(utilityService.deleteLegacyFiles(record));
        assertFalse(record.exists());
    }

    /**
     * The kept config.yml leaves its folder non-empty, so the folder itself survives and the
     * call reports false, though every other file in it is gone.
     */
    @Test
    public void aFolderHoldingConfigYml_keepsTheConfigAndTheFolder() throws Exception {
        File legacyFolder = temporaryFolder.newFolder("Medieval-Economy");
        File config = createFile(legacyFolder, "config.yml");
        createFile(legacyFolder, "coinpurse-record-filenames.txt");
        createFile(legacyFolder, "Steve.txt");

        assertFalse(utilityService.deleteLegacyFiles(legacyFolder));
        assertTrue(config.exists());
        assertArrayEquals(new String[]{"config.yml"}, legacyFolder.list());
    }

    @Test
    public void configYmlItself_isKeptAndReportedAsHandled() throws Exception {
        File config = createFile(temporaryFolder.getRoot(), "config.yml");

        assertTrue(utilityService.deleteLegacyFiles(config));
        assertTrue(config.exists());
    }

    /**
     * The check is a substring match on the absolute path, so a backup such as config.yml.old is
     * kept as well.
     */
    @Test
    public void aFileWhoseNameContainsConfigYml_isKept() throws Exception {
        File backup = createFile(temporaryFolder.getRoot(), "config.yml.old");

        assertTrue(utilityService.deleteLegacyFiles(backup));
        assertTrue(backup.exists());
    }

    /**
     * ensureSmoothTransitionBetweenVersions() renames the legacy folder before calling this, so
     * in that flow the path no longer exists by the time it is passed in.
     */
    @Test
    public void aPathThatDoesNotExist_reportsFalse() {
        File missing = new File(temporaryFolder.getRoot(), "Medieval-Economy");

        assertFalse(utilityService.deleteLegacyFiles(missing));
    }
}
