import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SongProxyTest {

    @Test
    public void testSearchByIdWithCacheHit() {
        SongProxy proxy = new SongProxy();

        // Search for song 1 first time
        Song song1 = proxy.searchById(1);
        assertNotNull(song1);
        assertEquals(1, song1.getId());

        // Search for song 1 again, should retrieve from cache
        Song cachedSong1 = proxy.searchById(1);
        assertNotNull(cachedSong1);
        assertEquals(1, cachedSong1.getId());
        assertSame(song1, cachedSong1); // Ensure it's the same object from cache
    }

    @Test
    public void testSearchByTitleWithCacheHit() {
        SongProxy proxy = new SongProxy();

        // Search for songs with title "Song 1"
        List<Song> songs1 = proxy.searchByTitle("Song 1");
        assertFalse(songs1.isEmpty());
        assertTrue(songs1.stream().anyMatch(song -> song.getTitle().equals("Song 1")));

        // Search for songs with title "Song 1" again, should retrieve from cache
        List<Song> cachedSongs1 = proxy.searchByTitle("Song 1");
        assertFalse(cachedSongs1.isEmpty());
        assertTrue(cachedSongs1.stream().anyMatch(song -> song.getTitle().equals("Song 1")));
        assertEquals(songs1, cachedSongs1); // Ensure it's the same list object from cache
    }

    @Test
    public void testSearchByAlbumWithCacheHit() {
        SongProxy proxy = new SongProxy();

        // Search for songs with album "Album 1"
        List<Song> album1Songs = proxy.searchByAlbum("Album 1");
        assertFalse(album1Songs.isEmpty());
        assertTrue(album1Songs.stream().anyMatch(song -> song.getAlbum().equals("Album 1")));

        // Search for songs with album "Album 1" again, should retrieve from cache
        List<Song> cachedAlbum1Songs = proxy.searchByAlbum("Album 1");
        assertFalse(cachedAlbum1Songs.isEmpty());
        assertTrue(cachedAlbum1Songs.stream().anyMatch(song -> song.getAlbum().equals("Album 1")));
        assertEquals(album1Songs, cachedAlbum1Songs); // Ensure it's the same list object from cache
    }

    @Test
    public void testMultipleSearchesWithCacheUpdates() {
        SongProxy proxy = new SongProxy();

        // Search for different songs and albums
        Song song1 = proxy.searchById(1);
        List<Song> song2List = proxy.searchByTitle("Song 2");
        List<Song> album2List = proxy.searchByAlbum("Album 2");

        // Ensure searches retrieve correct results
        assertNotNull(song1);
        assertEquals(1, song1.getId());
        assertFalse(song2List.isEmpty());
        assertTrue(song2List.stream().anyMatch(song -> song.getTitle().equals("Song 2")));
        assertFalse(album2List.isEmpty());
        assertTrue(album2List.stream().anyMatch(song -> song.getAlbum().equals("Album 2")));

        // Verify cache updates
        assertTrue(proxy.getCache().containsKey(1));
        assertTrue(proxy.getTitleCache().containsKey("Song 2"));
        assertTrue(proxy.getAlbumCache().containsKey("Album 2"));
    }
}