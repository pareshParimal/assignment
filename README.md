Map Data Processing Solution – Documentation
Project Overview
This application processes geographical data for places like restaurants, hotels, and cafes, along with their ratings and reviews. It helps analyze this data to find patterns and trends across different types of locations.
What the Application Does
In simple terms, this application:
Reads location details (ID, latitude, longitude) from a file.
Reads extra information (type, rating, reviews) from another file.
Combines both datasets using matching IDs.
Analyzes the combined data to generate useful insights.
Displays the results in a clear and easy-to-read format.
Key Features
Flexible Data Sources – Works with both JSON and CSV files.
Configurable File Locations – Uses a simple properties file to set file locations.
Comprehensive Analysis – Provides different insights about the data.
Data Validation – Detects and reports missing or incorrect data.
The Analysis Results
Once the data is processed, the application provides:
Count by Type – Number of locations for each category (restaurants, hotels, cafes, etc.).
Average Ratings – The average customer rating for each type of place.
Most Reviewed – The location with the highest number of reviews.
Data Quality Check – A report on missing or incomplete data.
How It Works
The application follows a simple process:
Configuration – Reads settings from a properties file to locate the data.
Data Loading – Reads location and metadata files.
Data Merging – Combines information based on matching IDs.
Analysis – Processes the data to calculate useful statistics.
Results – Displays the findings in a clear format.
Technical Approach
This solution is designed to be:
User-Friendly – Provides clear results and useful error messages.
Adaptable – Works with different file formats and configurations.
Maintainable – Well-organized code that’s easy to update.
Robust – Handles errors smoothly without crashing.
Using the Application
To use this application:
Place your location and metadata files in the correct directory.
Update the config.properties file with the correct file paths.
Run the application.
Review the results.
Example Output

Map Data Analysis Results  

1. Count of Valid Points per Type:  
   restaurant: 3  
   hotel: 3  
   cafe: 2  

2. Average Rating per Type:  
   restaurant: 4.10  
   hotel: 3.40  
   cafe: 4.60  

3. Location with Highest Number of Reviews:  
   ID: loc_07, Type: hotel, Reviews: 900  

4. Locations with Incomplete Data:  
   No complete data found.  





