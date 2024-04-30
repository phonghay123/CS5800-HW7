import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

interface SongService {
    Song searchById(Integer songID);
    List<Song> searchByTitle(String title);
    List<Song> searchByAlbum(String album);
}

class Song {
    private Integer id;
    private String title;
    private String artist;
    private String album;
    private int duration;

    public Song(Integer id, String title, String artist, String album, int duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", duration=" + duration +
                '}';
    }
}

class SongProxy implements SongService {
    private RealSongService realSongService;
    private Map<Integer, Song> cache;
    private Map<String, List<Song>> titleCache;
    private Map<String, List<Song>> albumCache;

    public SongProxy() {
        this.realSongService = new RealSongService();
        this.cache = new HashMap<>();
        this.titleCache = new HashMap<>();
        this.albumCache = new HashMap<>();
    }

    @Override
    public Song searchById(Integer songID) {
        if (cache.containsKey(songID)) {
            System.out.println("Retrieving song from cache...");
            return cache.get(songID);
        } else {
            Song song = realSongService.searchById(songID);
            cache.put(songID, song);
            return song;
        }
    }

    @Override
    public List<Song> searchByTitle(String title) {
        if (titleCache.containsKey(title)) {
            System.out.println("Retrieving songs from title cache...");
            return titleCache.get(title);
        } else {
            List<Song> songs = realSongService.searchByTitle(title);
            titleCache.put(title, songs);
            return songs;
        }
    }

    @Override
    public List<Song> searchByAlbum(String album) {
        if (albumCache.containsKey(album)) {
            System.out.println("Retrieving songs from album cache...");
            return albumCache.get(album);
        } else {
            List<Song> songs = realSongService.searchByAlbum(album);
            albumCache.put(album, songs);
            return songs;
        }
    }
}

public class SongProxyApp {
    public static void main(String[] args) {
        SongService proxy = new SongProxy();

        // Perform searches
        System.out.println(proxy.searchById(1));
        System.out.println(proxy.searchById(2));
        System.out.println(proxy.searchById(1));
        System.out.println(proxy.searchById(3));
        System.out.println(proxy.searchById(2));
        System.out.println(proxy.searchByTitle("Song 1"));
        System.out.println(proxy.searchByTitle("Song 2"));
        System.out.println(proxy.searchByTitle("Song 1"));
        System.out.println(proxy.searchByTitle("Song 2"));
        System.out.println(proxy.searchByAlbum("Album 1"));
        System.out.println(proxy.searchByAlbum("Album 2"));
        System.out.println(proxy.searchByAlbum("Album 1"));
        System.out.println(proxy.searchByAlbum("Album 2"));
    }
}

class RealSongService implements SongService {
    private List<Song> songs;

    public RealSongService() {
        this.songs = new ArrayList<>();
        // Simulating a database of songs
        songs.add(new Song(1, "Song 1", "Artist 1", "Album 1", 180));
        songs.add(new Song(2, "Song 2", "Artist 2", "Album 2", 200));
        songs.add(new Song(3, "Song 3", "Artist 3", "Album 3", 220));
        songs.add(new Song(4, "Song 4", "Artist 4", "Album 4", 220));
        songs.add(new Song(5, "Song 5", "Artist 5", "Album 5", 220));
        songs.add(new Song(6, "Song 6", "Artist 1", "Album 1", 180));
        songs.add(new Song(7, "Song 7", "Artist 2", "Album 2", 200));
        songs.add(new Song(8, "Song 8", "Artist 3", "Album 3", 220));
        songs.add(new Song(9, "Song 9", "Artist 4", "Album 4", 220));
        songs.add(new Song(10, "Song 10", "Artist 5", "Album 5", 220));
    }

    @Override
    public Song searchById(Integer songID) {
        try {
            Thread.sleep(1000); // Simulate server delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(Song song : songs)
        {
            if(song.getId() == songID)
            {
                return song;
            }
        }
        return null;
    }

    @Override
    public List<Song> searchByTitle(String title) {
        try {
            Thread.sleep(1000); // Simulate server delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Song> result = new ArrayList<>();
        for(Song song : songs)
        {
            if(song.getTitle().equals(title))
            {
                result.add(song);
            }
        }
        return result;
    }

    @Override
    public List<Song> searchByAlbum(String album) {
        try {
            Thread.sleep(1000); // Simulate server delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Song> result = new ArrayList<>();
        for(Song song : songs)
        {
            if(song.getAlbum().equals(album))
            {
                result.add(song);
            }
        }
        return result;
    }
}
