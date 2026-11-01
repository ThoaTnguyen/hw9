package patterns.proxy;

import java.util.List;

public interface SongService {
    Song searchById(Integer songID);

    List<Song> searchByTitle(String title);

    List<Song> searchByAlbum(String album);
}
