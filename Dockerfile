# ========================================================================
# STAGE 1: BUILD STAGE (Layihəni yığmaq üçün)
#========================================================================
# Gradle toolu ve JDK daxil olan imici istifade edirik
FROM gradle:8.9-jdk17-jammy AS build

# Konteyner daxilindeki iş qovluğu
WORKDIR /app

# Gradle fayllarini kopyala
COPY build.gradle settings.gradle /app/

# Kodun özünü kopyala
COPY src /app/src

# Layihəni yığ (Build)
RUN gradle clean build -x test

# ========================================================================
# STAGE 2: PRODUCTION STAGE (Yalnız JRE)
# ========================================================================
# Yüngül JRE (Java Runtime Environment) imicini istifade edirik
FROM eclipse-temurin:17-jre-jammy

# Konteyner daxilindeki iş qovluğu
WORKDIR /app

# Build stage-den yaranan JAR faylini kopyala
COPY --from=build /app/build/libs/*.jar app.jar

# Spring tətbiqini işə salmaq üçün komanda
ENTRYPOINT ["java", "-jar", "app.jar"]