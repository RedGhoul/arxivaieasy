package com.ava.arxivai.scheduledtasks;

import com.ava.arxivai.domain.Author;
import com.ava.arxivai.domain.Paper;
import com.ava.arxivai.domain.Subject;
import com.ava.arxivai.repository.AuthorRepository;
import com.ava.arxivai.repository.PaperRepository;
import com.ava.arxivai.repository.SubjectRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class ScrapTask {

    private static final Logger log = LoggerFactory.getLogger(ScrapTask.class);
    private final PaperRepository paperRepository;
    private final AuthorRepository authorRepository;
    private final SubjectRepository subjectRepository;

    public ScrapTask(PaperRepository paperRepository, AuthorRepository authorRepository, SubjectRepository subjectRepository) {
        this.paperRepository = paperRepository;
        this.authorRepository = authorRepository;
        this.subjectRepository = subjectRepository;
    }

    @Scheduled(cron = "0 12 10 * * ?")
    public void getDateFromSite() throws IOException, InterruptedException {
        String baseUrl = "https://arxiv.org";

        List<String> TwoValues = Arrays.asList("/list/cs.LG/pastweek?show=300", "/list/cs.AI/pastweek?show=300");

        for (String listFragment : TwoValues) {
            Document doc = Jsoup.connect(baseUrl + listFragment).get();
            List<String> baseUrls = new ArrayList<>();
            Elements newsHeadlines = doc.select(".list-identifier");
            for (Element headline : newsHeadlines) {
                if (headline.parentNode() != null) {
                    var child = headline.childNode(0);
                    var mainUrl = child.attr("href");
                    baseUrls.add(baseUrl + mainUrl);
                }
            }

            for (String curBaseUrl : baseUrls) {
                TimeUnit.SECONDS.sleep(30);
                Document newDoc = Jsoup.connect(curBaseUrl).get();
                var arxivPDFLinkNode = newDoc.select(".abs-button.download-pdf");
                var arxivPDFLinkText = baseUrl + arxivPDFLinkNode.attr("href");
                var arxivTitleNode = newDoc.select(".title");
                var arxivTitleText = arxivTitleNode.get(0).childNode(1).toString();
                var arxivAuthors = newDoc.select(".authors");
                var arxivAuthorsMap = new HashMap<String, String>();
                for (Node author : arxivAuthors.get(0).childNodes()) {
                    String fullNameHyperLink = author.attributes().get("href");
                    if (!fullNameHyperLink.isEmpty()) {
                        String fullName = author.childNode(0).toString();
                        arxivAuthorsMap.put(fullName, fullNameHyperLink);
                    }
                }

                Elements arxivAbstract = newDoc.select(".abstract");
                String arxivAbstractText = arxivAbstract.get(0).childNode(2).toString();

                Set<Subject> subjectHashSet = new HashSet<>();

                Elements arxivSubjects2 = newDoc.select(".tablecell.subjects");
                var subjectNodes = arxivSubjects2.get(0).childNodes();

                for (Node subject : subjectNodes) {
                    String arxivSubject1 = subject.toString();
                    arxivSubject1 = Jsoup.parse(arxivSubject1).text();
                    var arxivSubjectArray = arxivSubject1.split(";");
                    if (arxivSubjectArray.length > 0) {
                        for (String curSubject : arxivSubjectArray) {
                            curSubject = curSubject.trim();
                            if (!curSubject.isBlank()) {
                                if (!subjectRepository.existsSubjectByTitle(curSubject)) {
                                    Subject newSubject = new Subject();
                                    newSubject.title(curSubject);
                                    subjectRepository.save(newSubject);
                                    subjectHashSet.add(newSubject);
                                } else {
                                    subjectHashSet.add(subjectRepository.findSubjectByTitle(curSubject));
                                }
                            }
                        }
                    }
                }

                Set<Author> auths = new HashSet<>();
                for (Map.Entry<String, String> entry : arxivAuthorsMap.entrySet()) {
                    if (!authorRepository.existsAuthorByName(entry.getKey())) {
                        Author newAut = new Author();
                        newAut.name(entry.getKey());
                        newAut.historyLink(entry.getValue());
                        try {
                            authorRepository.save(newAut);
                        } catch (Exception e) {
                            log.info(e.getMessage());
                        }

                        auths.add(newAut);
                    }
                }

                if (!paperRepository.existsByAbstractText(arxivAbstractText)) {
                    Paper curPaper = new Paper();
                    curPaper.abstractText(arxivAbstractText);

                    curPaper.title(arxivTitleText);
                    curPaper.setPdfLink(arxivPDFLinkText);
                    curPaper.setAuthors(auths);
                    curPaper.createdDate(LocalDate.now());
                    curPaper.setBaseLink(curBaseUrl);
                    curPaper.setSubjects(subjectHashSet);
                    var dateLine = newDoc.select(".dateline");

                    if (dateLine != null) {
                        String replaceText = "\\n  \\n  \\n  \\n    \\n  \\n  \\n    \\n    \\n  \\n\\n  [Submitted on ";
                        var date = dateLine.text();
                        date = date.replace(replaceText, "");
                        date = date.replace("]", "").replace("[", "");
                        curPaper.setSubmitedDate(date);
                    }
                    try {
                        paperRepository.save(curPaper);
                    } catch (Exception e) {
                        log.info(e.getMessage());
                    }
                }
            }
        }
    }
}
