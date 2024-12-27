package com.example;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovieTracker {

    private final JFrame frame;
    private final JTable movieTable;
    private final DefaultTableModel tableModel;
    private final ArrayList<Movie> movies;
    private static final String API_KEY = "5adfb33949e1d123bec22b838e987e6b";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MovieTracker::new);
    }

    public MovieTracker() {
        movies = new ArrayList<>();

        frame = new JFrame("Movie Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        tableModel = new DefaultTableModel(new String[]{"Title", "Synopsis", "Duration", "Rating", "Status", "Poster"}, 0);
        movieTable = new JTable(tableModel);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setRowHeight(100);
        movieTable.getColumnModel().getColumn(5).setCellRenderer(new ImageRenderer());

        JScrollPane scrollPane = new JScrollPane(movieTable);

        JButton addButton = new JButton("Add Movie");
        addButton.addActionListener(e -> showAddMovieDialog());

        JButton deleteButton = new JButton("Delete Movie");
        deleteButton.addActionListener(e -> deleteSelectedMovie());

        JButton detailsButton = new JButton("View Details");
        detailsButton.addActionListener(e -> showMovieDetails());

        JButton toggleStatusButton = new JButton("Toggle Status");
        toggleStatusButton.addActionListener(e -> toggleMovieStatus());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(detailsButton);
        buttonPanel.add(toggleStatusButton); // Add the new button here

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void showAddMovieDialog() {
        JTextField titleField = new JTextField();

        Object[] message = {
                "Enter Movie Title:", titleField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Add Movie", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText();
            Movie movie = getMovieDetailsFromAPI(title);

            if (movie != null) {
                movies.add(movie);
                updateTable();
                JOptionPane.showMessageDialog(frame, "Movie added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Movie not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedMovie() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow >= 0) {
            movies.remove(selectedRow);
            updateTable();
            JOptionPane.showMessageDialog(frame, "Movie deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a movie to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMovieDetails() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow >= 0) {
            Movie movie = movies.get(selectedRow);
            ImageIcon poster = loadImageIcon(movie.posterPath);
            JLabel posterLabel = new JLabel();

            if (poster != null) {
                posterLabel.setIcon(poster);
            } else {
                posterLabel.setText("No Image Available");
            }

            String details = String.format(
                    "<html><h2>%s</h2><p><b>Synopsis:</b> %s</p><p><b>Duration:</b> %d minutes</p><p><b>Rating:</b> %.1f</p><p><b>Status:</b> %s</p></html>",
                    movie.title, wrapText(movie.synopsis, 80), movie.duration, movie.rating, movie.getStatus());
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.add(posterLabel, BorderLayout.WEST);
            panel.add(new JLabel(details), BorderLayout.CENTER);

            JOptionPane.showMessageDialog(frame, panel, "Movie Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a movie to view details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String wrapText(String text, int length) {
        StringBuilder wrapped = new StringBuilder();
        String[] words = text.split(" ");
        int lineLength = 0;
        for (String word : words) {
            if (lineLength + word.length() > length) {
                wrapped.append("<br>");
                lineLength = 0;
            }
            wrapped.append(word).append(" ");
            lineLength += word.length() + 1;
        }
        return wrapped.toString();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Movie movie : movies) {
            tableModel.addRow(new Object[]{
                    movie.title,
                    movie.synopsis,
                    movie.duration,
                    movie.rating,
                    movie.getStatus(),
                    loadImageIcon(movie.posterPath)
            });
        }
    }

    private ImageIcon loadImageIcon(String imageUrl) {
        try {
            Image image = Toolkit.getDefaultToolkit().createImage(new java.net.URL(imageUrl));
            return new ImageIcon(image.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Movie getMovieDetailsFromAPI(String movieTitle) {
        String searchUrl = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query=" + java.net.URLEncoder.encode(movieTitle, java.nio.charset.StandardCharsets.UTF_8);
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(searchUrl).toURL().openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray results = jsonResponse.getJSONArray("results");
                if (results.length() > 0) {
                    JSONObject firstResult = results.getJSONObject(0);
                    String title = firstResult.getString("title");
                    String synopsis = firstResult.getString("overview");
                    double rating = firstResult.getDouble("vote_average");
                    String posterPath = firstResult.getString("poster_path");
                    int movieId = firstResult.getInt("id");

                    String detailUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
                    return new Movie(title, synopsis, 120, rating, false, detailUrl);
                }
            }
        } catch (IOException | JSONException e) {
            Logger.getLogger(MovieTracker.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    private void toggleMovieStatus() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow >= 0) {
            Movie selectedMovie = movies.get(selectedRow);
            selectedMovie.toggleStatus();  // Toggle the status

            // Update the table to reflect the status change
            updateTable();
            JOptionPane.showMessageDialog(frame, "Movie status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a movie to toggle status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class Movie {
        String title;
        String synopsis;
        int duration;
        double rating;
        boolean watched;  // true if watched, false if "to watch"
        String posterPath;

        public Movie(String title, String synopsis, int duration, double rating, boolean watched, String posterPath) {
            this.title = title;
            this.synopsis = synopsis;
            this.duration = duration;
            this.rating = rating;
            this.watched = watched;
            this.posterPath = posterPath;
        }

        public String getStatus() {
            return watched ? "Watched" : "To Watch";
        }

        // Toggle the watched status between "Watched" and "To Watch"
        public void toggleStatus() {
            this.watched = !this.watched;
        }
    }

    static class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof ImageIcon) {
                JLabel label = new JLabel((ImageIcon) value);
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
