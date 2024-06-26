# Use the official Tomcat base image
FROM tomcat:11.0-jre17

# Copy the WAR file to the webapps directory of Tomcat
COPY target/MovieSeeker-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080 to the outside world
EXPOSE 8080

# Run Tomcat
CMD ["catalina.sh", "run"]
