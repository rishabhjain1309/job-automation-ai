# Job Automation AI

Automates job search, filtering, resume tailoring, and applications using AI.

## Tech Stack
- Java (Spring Boot)
- Ollama (Local LLM)
- n8n (Automation)
# 🤖 AutoJobApply

An AI-powered job search and auto-application bot built with Java, Spring Boot, Selenium, and Groq AI. It searches for relevant jobs across multiple platforms, matches them against your resume using AI, generates tailored cover letters, and automatically applies — all with a single API call.

---

## ✨ Features

- **Multi-platform job search** — Naukri, LinkedIn, Instahyre, Remotive, RemoteOK
- **AI resume parsing** — Extracts skills, experience, and roles from your resume text using Groq (LLaMA 3.1)
- **Smart job matching** — Scores each job based on role relevance and skill overlap; rejects irrelevant roles automatically
- **Auto-apply with Selenium** — Fills forms and submits applications on Naukri and LinkedIn Easy Apply
- **AI-generated cover letters** — Tailored cold email for each job using your profile and the job description
- **Excel report** — Generates a timestamped `.xlsx` file with all matched jobs, scores, and apply status
- **Duplicate prevention** — H2 database tracks applied jobs so you never apply to the same listing twice
- **Human-like behavior** — Random delays, JS clicks, navbar hiding to avoid bot detection

---

## 🏗️ Architecture

```
AutoJobApply/
├── config/
│   └── SeleniumConfig.java          # Creates a fresh Chrome driver per run
│
├── controller/
│   ├── JobController.java           # Main API endpoints
│   ├── EmailController.java         # Send emails manually
│   └── ResumeController.java        # Parse resume endpoint
│
├── model/
│   ├── Job.java                     # Job data (title, company, url, description)
│   ├── ResumeProfile.java           # Parsed resume (skills, experience, roles)
│   ├── JobMatchResult.java          # Match score and decision
│   ├── CandidateProfile.java        # Your personal info loaded from application.properties
│   ├── AppliedJob.java              # DB entity to track applied jobs
│   └── EmailRequest.java
│
├── repository/
│   └── AppliedJobRepository.java    # JPA repo — checks if already applied
│
├── service/
│   ├── JobApplicationService.java   # 🎯 Main orchestrator — runs all 7 phases
│   ├── JobMatchingService.java      # Scores jobs against resume
│   ├── JobService.java              # Fetches from Remotive + RemoteOK APIs
│   ├── ResumeParserService.java     # AI resume parsing via Groq
│   ├── AIService.java               # Generates cold emails and summaries
│   ├── ExcelService.java            # Creates timestamped Excel reports
│   ├── EmailService.java            # Sends emails via Gmail SMTP
│   ├── OllamaService.java           # Groq API HTTP client
│   └── selenium/
│       ├── SeleniumHelper.java      # Shared utils (JS click, human typing, delays)
│       ├── NaukriService.java       # Naukri login + search + apply
│       ├── LinkedInService.java     # LinkedIn login + Easy Apply
│       └── InstahyreService.java    # Instahyre login + search + apply
│
└── resources/
    └── application.properties       # All config: credentials, candidate profile, DB
```

---

## ⚙️ How It Works — 7 Phases

When you call `POST /api/run-job-automation` with your resume text, the system runs:

```
Phase 1 → Parse resume with AI (Groq LLaMA 3.1)
Phase 2 → Collect jobs from all platforms (Naukri × 4 keywords, Instahyre, LinkedIn, Remotive)
Phase 3 → Deduplicate by URL
Phase 4 → Match each job against resume — score 0–100%, reject irrelevant roles
Phase 5 → Sort by score descending
Phase 6 → Save Excel report (timestamped jobs_YYYY-MM-DD_HH-mm-ss.xlsx)
Phase 7 → Apply platform by platform (Naukri first, then LinkedIn, then Instahyre)
```

---

## 🚀 Getting Started

### Prerequisites

| Requirement | Version |
|---|---|
| Java | 17+ (tested on 25) |
| Maven | 3.8+ |
| Google Chrome | Latest (146+) |
| Groq API Key | Free at [console.groq.com](https://console.groq.com) |

### 1. Clone and open

```bash
git clone https://github.com/your-username/AutoJobApply.git
cd AutoJobApply
```

Open in IntelliJ IDEA or your preferred IDE.

### 2. Configure `application.properties`

Edit `src/main/resources/application.properties` — fill in every section:

```properties
# ── Groq AI (free) ────────────────────────────────────────────────────────
groq.api.key=your_groq_api_key_here

# ── Gmail (use App Password, not your real password) ──────────────────────
spring.mail.username=your_email@gmail.com
spring.mail.password=your_16_char_app_password

# ── Platform Credentials ──────────────────────────────────────────────────
naukri.email=your_naukri_email
naukri.password=your_naukri_password

instahyre.email=your_instahyre_email
instahyre.password=your_instahyre_password

linkedin.email=your_linkedin_email
linkedin.password=your_linkedin_password

# ── Your Profile (fills all job application forms automatically) ───────────
candidate.full-name=Your Full Name
candidate.phone=9999999999
candidate.current-job-title=Java Backend Developer
candidate.total-experience-years=2
candidate.current-ctc-lpa=7
candidate.expected-ctc-lpa=18
candidate.notice-period=60 Days
candidate.primary-skills=Java,Spring Boot,Kafka,Microservices,REST API
# ... (see full template below)
```

> **Gmail App Password:** Go to Google Account → Security → 2-Step Verification → App Passwords → Generate one for "Mail".

### 3. Add Maven dependencies

Make sure your `pom.xml` includes:

```xml
<!-- Selenium -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.31.0</version>
</dependency>

<!-- WebDriverManager (auto-downloads ChromeDriver) -->
<dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.9.2</version>
</dependency>

<!-- Apache POI (Excel) -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>

<!-- H2 Database -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Spring Boot starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

### 4. Run the app

```bash
mvn spring-boot:run
```

You should see:
```
Started AutoJobApplyApplication in 4.4 seconds
H2 console available at '/h2-console'
```

---

## 📡 API Endpoints

### Run full automation
```
POST http://localhost:8080/api/run-job-automation
Content-Type: text/plain

[paste your resume text here]
```

This is the main endpoint. It opens Chrome, logs into platforms, collects jobs, matches, applies, and saves an Excel file.

### Search only (no apply)
```
POST http://localhost:8080/api/run-job-search
Content-Type: text/plain

[paste your resume text here]
```

Collects and matches jobs, saves Excel — does not apply to anything. Good for reviewing before auto-applying.

### Parse resume
```
POST http://localhost:8080/parse-resume
Content-Type: text/plain

[paste your resume text here]
```

Returns JSON with extracted skills, experience, roles, and tools.

### Test job match
```
POST http://localhost:8080/api/match-job
Content-Type: application/json

{
  "resume": "Java developer with Spring Boot experience...",
  "jobDescription": "We need a Java backend engineer with microservices..."
}
```

### Send test email
```
POST http://localhost:8080/email/send
Content-Type: application/json

{
  "to": "recruiter@company.com",
  "subject": "Application for Java Developer",
  "body": "Dear Hiring Manager..."
}
```

---

## 📊 Excel Output

Each run generates a timestamped file like `jobs_2026-03-30_14-35-11.xlsx` with these columns:

| Column | Description |
|---|---|
| Title | Job title |
| Company | Company name |
| Score (%) | Match score 0–100 |
| Decision | APPLY or SKIP |
| Matched Skills | Your skills found in the JD |
| Missing Skills | Your skills not found in JD |
| Platform | NAUKRI / LINKEDIN / INSTAHYRE / EXTERNAL |
| URL | Direct link to the job posting |

---

## 🧠 Matching Algorithm

The job matching uses a two-factor scoring system:

**Role Score (0–60 points)** — Is this job relevant to Java backend?
- Checks if title/JD contains terms like: `java`, `spring boot`, `backend`, `microservice`, `software engineer`, `sde`, `full stack`
- Each matching term adds 12 points, capped at 60

**Skill Score (0–100 points)** — Do your resume skills appear in the JD?
- Only meaningful when the JD is longer than 150 characters
- `matched_skills / total_skills × 100`

**Final Score:**
- Short JD (< 150 chars, card snippet): role score only
- Full JD: `(role score + skill score) / 2`

**Hard Reject** — Jobs containing these terms are immediately skipped regardless of score:
`reactjs`, `golang`, `php developer`, `android developer`, `data scientist`, `qa engineer`, `product manager`, `.net developer`, and 20+ more irrelevant categories.

**Threshold:** Jobs scoring ≥ 40% are marked APPLY and added to Excel. Jobs below 40% are SKIP.

---

## 🔧 Candidate Profile — Full Template

Copy this into `application.properties` and fill in your details:

```properties
# Personal Info
candidate.full-name=Your Full Name
candidate.email=your@email.com
candidate.phone=9999999999
candidate.linkedin-url=https://linkedin.com/in/yourprofile
candidate.github-url=https://github.com/yourusername
candidate.portfolio-url=
candidate.current-city=Bangalore
candidate.current-state=Karnataka
candidate.pincode=560001
candidate.nationality=Indian
candidate.gender=Male
candidate.date-of-birth=DD/MM/YYYY

# Job Preferences
candidate.current-job-title=Java Backend Developer
candidate.preferred-job-title=Senior Java Developer,Backend Developer,Software Engineer,SDE2
candidate.preferred-locations=Bangalore,Pune,Remote,Hyderabad
candidate.open-to-remote=true
candidate.open-to-relocate=true
candidate.job-type=Full-time

# Experience & Education
candidate.total-experience-years=2
candidate.total-experience-months=6
candidate.highest-degree=B.Tech
candidate.degree-branch=Computer Science
candidate.college-name=Your College
candidate.passing-year=2023
candidate.cgpa=8.5

# Current Employment
candidate.current-company=Your Current Company
candidate.current-employment-type=Full-time
candidate.currently-employed=true
candidate.notice-period=60 Days

# Salary (in LPA)
candidate.current-ctc-lpa=7
candidate.expected-ctc-lpa=18
candidate.ctc-negotiable=true

# Skills (comma-separated, no spaces after commas)
candidate.primary-skills=Java,Spring Boot,Kafka,Microservices,REST API
candidate.secondary-skills=Docker,Git,AWS,MySQL,Hibernate
candidate.certifications=

# Work Authorization
candidate.authorized-to-work-in-india=true
candidate.requires-visa=false
candidate.differently-abled=false
candidate.veteran-status=Not a veteran

# Cover Letter Content (AI uses these to write emails)
candidate.career-summary=Java Backend Developer with 2+ years building scalable microservices using Spring Boot and Kafka.
candidate.why-looking-for-change=Seeking a more challenging role with growth opportunities in distributed systems.
candidate.achievement-highlight=Reduced API response time by 40% by redesigning the caching layer using Redis.
```

---

## ⚠️ Important Notes

### First-time LinkedIn login
LinkedIn detects new devices and shows an OTP/CAPTCHA. The app waits 30 seconds when it detects a checkpoint — complete the verification manually in the browser window during that time. After the first successful login, subsequent runs won't trigger the checkpoint.

### Naukri form fields
Naukri's apply modal asks for CTC, notice period, and sometimes a cover letter. The app fills these automatically from your `candidate.*` properties using JavaScript injection to bypass the sticky header that blocks normal clicks.

### Rate limiting
The app adds 3–6 second random delays between each job application to mimic human behavior and avoid getting flagged. Do not run more than 50–60 applications per day on any single platform.

### Browser visibility
The browser runs in visible mode by default so you can monitor what's happening. To run headlessly (no window), uncomment this line in `SeleniumConfig.java`:
```java
// options.addArguments("--headless=new");
```

### Applied jobs tracking
The H2 in-memory database resets every time you restart the application (`create-drop` mode). To persist applied jobs across restarts, switch to file-based H2:
```properties
spring.datasource.url=jdbc:h2:file:./appliedjobs;DB_CLOSE_DELAY=-1
spring.jpa.hibernate.ddl-auto=update
```

---

## 🛠️ Troubleshooting

| Problem | Cause | Fix |
|---|---|---|
| `AppliedJobRepository bean not found` | `spring.autoconfigure.exclude=DataSourceAutoConfiguration` present | Remove that line from `application.properties` |
| `invalid session id` | Chrome closed between runs but Spring kept the old driver bean | Ensure `SeleniumConfig` is a `@Component` with `createDriver()` method, NOT a `@Bean` |
| `element click intercepted` on Naukri | Sticky navbar covers buttons | `hideNavbar()` is called automatically — ensure you have the latest `NaukriService` |
| LinkedIn Easy Apply = 0 jobs | Easy Apply badge not in DOM | App uses `f_AL=true` URL param which forces Easy Apply filter; badge check removed |
| Excel empty / 0 matched jobs | Score threshold too high or resume parsing failed | Check Phase 1 log — if skills list is wrong, the fallback uses `candidate.primary-skills` |
| CDP warning on startup | Selenium 4.31 doesn't bundle devtools for Chrome 146 | Safe to ignore — does not affect functionality |

---

## 🗺️ Roadmap

- [ ] **Company career sites** — auto-detect Greenhouse, Lever, Workday ATS and fill forms
- [ ] **Google Jobs scraper** — collect from Google Jobs search results
- [ ] **Monster.com** support
- [ ] **PDF resume upload** — parse from `.pdf` instead of raw text
- [ ] **Daily scheduler** — run automatically every morning with `@Scheduled`
- [ ] **Telegram/email notifications** — get notified when jobs are applied
- [ ] **Dashboard UI** — React frontend to monitor jobs and results
- [ ] **Persistent DB** — switch to PostgreSQL for production use

---

## 📄 License

MIT License — free to use, modify, and distribute.

---

## 🙏 Tech Stack

| Technology | Purpose |
|---|---|
| Java 17+ / Spring Boot 3.5 | Application framework |
| Selenium 4.31 + WebDriverManager | Browser automation |
| Groq API (LLaMA 3.1 8B) | Resume parsing, cover letter generation |
| Apache POI | Excel report generation |
| H2 Database + Spring Data JPA | Applied jobs tracking |
| Spring Mail + Gmail SMTP | Email applications |
| Remotive API + RemoteOK API | Free job listing sources |
