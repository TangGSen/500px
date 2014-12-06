# 500px Browser

- this app allows you to browse images from 500px by feature
    - 'popular' — Return photos in Popular.  Default sort: rating.
    - 'highest_rated' — Return photos that have been in Popular.  Default sort: highest_rating.
    - 'upcoming' — Return photos in Upcoming.  Default sort: time when Upcoming was reached.
    - 'editors' — Return photos in Editors' Choice.  Default sort: time when selected by an editor.
    - 'fresh_today' — Return photos in Fresh Today.  Default sort: time when reached fresh.
    - 'fresh_yesterday' — Return photos in Fresh Yesterday.  Default sort: same as 'fresh_today'.
    - 'fresh_week' — Return photos in Fresh This Week.  Default sort: same as 'fresh_today'.
- You can download an image by clicking on it
- And view it by clicking again
- You can cancel a download, or delete an image by long pressing
- Images are stored in your external media
- The downloader is an `IntentService`.


**Note:** This app does not use Picasso, rather I implemented a very simple image downloading mechanism, which works well with listadapters. See `ImageCacher.java`