package com.bilanprojets.service.impl;

import com.bilanprojets.dto.BilanAnnuelDTO;
import com.bilanprojets.dto.FonctionnaliteDTO;
import com.bilanprojets.dto.SituationProjetFicheDTO;
import com.bilanprojets.dto.SyntheseAnnuelleDTO;
import com.bilanprojets.entity.*;
import com.bilanprojets.exception.BusinessException;
import com.bilanprojets.exception.ResourceNotFoundException;
import com.bilanprojets.repository.BilanAnnuelRepository;
import com.bilanprojets.repository.FonctionnaliteRepository;
import com.bilanprojets.repository.ProjetRepository;
import com.bilanprojets.repository.UtilisateurRepository;
import com.bilanprojets.service.BilanAnnuelService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BilanAnnuelServiceImpl implements BilanAnnuelService {

    private final BilanAnnuelRepository bilanAnnuelRepository;
    private final ProjetRepository projetRepository;
    private final FonctionnaliteRepository fonctionnaliteRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    public BilanAnnuelDTO enregistrerBilan(BilanAnnuelDTO dto) {
        Projet projet = projetRepository.findById(dto.getProjetId())
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable avec l'ID : " + dto.getProjetId()));

        Optional<BilanAnnuel> existant = bilanAnnuelRepository.findByProjetIdAndAnnee(dto.getProjetId(), dto.getAnnee());
        if (existant.isPresent()) {
            throw new BusinessException("Un bilan existe déjà pour le projet '" + projet.getIntitule() + "' pour l'année " + dto.getAnnee());
        }

        BilanAnnuel bilan = BilanAnnuel.builder()
                .projet(projet)
                .annee(dto.getAnnee())
                .situationProjet(dto.getSituationProjet())
                .actionsAnneeEnCours(dto.getActionsAnneeEnCours())
                .situationDetails(dto.getSituationDetails())
                .fonctionnalitesPrincipales(dto.getFonctionnalitesPrincipales())
                .perspectivesAnneeSuivante(dto.getPerspectivesAnneeSuivante())
                .commentaires(dto.getCommentaires())
                .build();

        BilanAnnuel saved = bilanAnnuelRepository.save(bilan);
        return mapToDTO(saved);
    }

    @Override
    public BilanAnnuelDTO modifierBilan(Long id, BilanAnnuelDTO dto) {
        BilanAnnuel bilan = bilanAnnuelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bilan annuel introuvable avec l'ID : " + id));

        bilan.setSituationProjet(dto.getSituationProjet());
        bilan.setActionsAnneeEnCours(dto.getActionsAnneeEnCours());
        bilan.setSituationDetails(dto.getSituationDetails());
        bilan.setFonctionnalitesPrincipales(dto.getFonctionnalitesPrincipales());
        bilan.setPerspectivesAnneeSuivante(dto.getPerspectivesAnneeSuivante());
        bilan.setCommentaires(dto.getCommentaires());

        BilanAnnuel updated = bilanAnnuelRepository.save(bilan);
        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public BilanAnnuelDTO getBilanById(Long id) {
        BilanAnnuel bilan = bilanAnnuelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bilan annuel introuvable avec l'ID : " + id));
        return mapToDTO(bilan);
    }

    @Override
    @Transactional(readOnly = true)
    public BilanAnnuelDTO getBilanByProjetEtAnnee(Long projetId, Integer annee) {
        BilanAnnuel bilan = bilanAnnuelRepository.findByProjetIdAndAnnee(projetId, annee)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun bilan trouvé pour le projet ID " + projetId + " et l'année " + annee));
        return mapToDTO(bilan);
    }

    @Override
    @Transactional(readOnly = true)
    public SituationProjetFicheDTO getSituationProjetFiche(Long projetId, Integer annee) {
        Projet projet = projetRepository.findByIdWithDetails(projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable avec l'ID : " + projetId));

        BilanAnnuel bilan = bilanAnnuelRepository.findByProjetIdAndAnnee(projetId, annee)
                .orElse(null);

        List<Fonctionnalite> fonctionnalites = fonctionnaliteRepository.findByProjetId(projetId);

        return buildFicheDTO(projet, bilan, fonctionnalites, annee);
    }

    @Override
    public SituationProjetFicheDTO enregistrerFicheComplete(SituationProjetFicheDTO ficheDTO) {
        Projet projet;
        if (ficheDTO.getProjetId() != null) {
            projet = projetRepository.findById(ficheDTO.getProjetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable avec l'ID : " + ficheDTO.getProjetId()));
            projet.setIntitule(ficheDTO.getIntitule());
            projet.setDescription(ficheDTO.getDescription());
            if (ficheDTO.getTypeProjet() != null) {
                projet.setTypeProjet(ficheDTO.getTypeProjet());
            }
            if (ficheDTO.getAnneeDebut() != null) {
                projet.setAnneeDebut(ficheDTO.getAnneeDebut());
            }
            if (ficheDTO.getAnneeFinPrevue() != null) {
                projet.setAnneeFinPrevue(ficheDTO.getAnneeFinPrevue());
            }
            if (ficheDTO.getResponsableId() != null) {
                Utilisateur responsable = utilisateurRepository.findById(ficheDTO.getResponsableId()).orElse(null);
                projet.setResponsable(responsable);
            }
        } else {
            String code = (ficheDTO.getCodeProjet() != null && !ficheDTO.getCodeProjet().isBlank())
                    ? ficheDTO.getCodeProjet().trim().toUpperCase()
                    : "PRJ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            Utilisateur responsable = null;
            if (ficheDTO.getResponsableId() != null) {
                responsable = utilisateurRepository.findById(ficheDTO.getResponsableId()).orElse(null);
            }

            projet = Projet.builder()
                    .code(code)
                    .intitule(ficheDTO.getIntitule())
                    .description(ficheDTO.getDescription())
                    .typeProjet(ficheDTO.getTypeProjet() != null ? ficheDTO.getTypeProjet() : TypeProjet.PLURIANNUEL)
                    .anneeDebut(ficheDTO.getAnneeDebut())
                    .anneeFinPrevue(ficheDTO.getAnneeFinPrevue())
                    .responsable(responsable)
                    .build();
            projet = projetRepository.save(projet);
        }

        final Projet savedProjet = projet;
        final Integer annee = ficheDTO.getAnnee();

        BilanAnnuel bilan = bilanAnnuelRepository.findByProjetIdAndAnnee(savedProjet.getId(), annee)
                .orElseGet(() -> BilanAnnuel.builder()
                        .projet(savedProjet)
                        .annee(annee)
                        .build());

        bilan.setSituationProjet(ficheDTO.getSituationProjet());
        bilan.setActionsAnneeEnCours(ficheDTO.getActionsAnneeEnCours());
        bilan.setFonctionnalitesPrincipales(ficheDTO.getFonctionnalitesPrincipales());
        bilan.setCommentaires(ficheDTO.getCommentaires());
        BilanAnnuel savedBilan = bilanAnnuelRepository.save(bilan);

        List<Fonctionnalite> savedFonctionnalites = new ArrayList<>();
        if (ficheDTO.getListeFonctionnalites() != null && !ficheDTO.getListeFonctionnalites().isEmpty()) {
            for (FonctionnaliteDTO fDto : ficheDTO.getListeFonctionnalites()) {
                Fonctionnalite f;
                if (fDto.getId() != null) {
                    f = fonctionnaliteRepository.findById(fDto.getId())
                            .orElse(new Fonctionnalite());
                } else {
                    f = new Fonctionnalite();
                }
                f.setProjet(savedProjet);
                f.setBilanAnnuel(savedBilan);
                f.setCode(fDto.getCode() != null ? fDto.getCode() : "FCT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
                f.setIntitule(fDto.getIntitule());
                f.setDescription(fDto.getDescription());
                f.setStatut(fDto.getStatut() != null ? fDto.getStatut() : StatutFonctionnalite.A_FAIRE);
                f.setEstPrincipale(fDto.getEstPrincipale() != null ? fDto.getEstPrincipale() : false);
                f.setPriorite(fDto.getPriorite() != null ? fDto.getPriorite() : "MOYENNE");

                savedFonctionnalites.add(fonctionnaliteRepository.save(f));
            }
        } else {
            savedFonctionnalites = fonctionnaliteRepository.findByProjetId(savedProjet.getId());
        }

        return buildFicheDTO(savedProjet, savedBilan, savedFonctionnalites, annee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BilanAnnuelDTO> getBilansByAnnee(Integer annee) {
        return bilanAnnuelRepository.findAllByAnneeWithProjet(annee).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BilanAnnuelDTO> getBilansByProjet(Long projetId) {
        return bilanAnnuelRepository.findByProjetId(projetId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BilanAnnuelDTO> getBilansByAnneeEtSituation(Integer annee, SituationProjet situation) {
        return bilanAnnuelRepository.findByAnneeAndSituationProjet(annee, situation).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SyntheseAnnuelleDTO getSyntheseAnnuelle(Integer annee) {
        List<BilanAnnuel> bilans = bilanAnnuelRepository.findAllByAnneeWithProjet(annee);
        List<SituationProjetFicheDTO> fiches = new ArrayList<>();

        Map<SituationProjet, Long> repartition = new EnumMap<>(SituationProjet.class);
        for (SituationProjet s : SituationProjet.values()) {
            repartition.put(s, 0L);
        }

        int totalFonct = 0;
        int totalFonctTerminees = 0;

        for (BilanAnnuel b : bilans) {
            SituationProjet sit = b.getSituationProjet();
            repartition.put(sit, repartition.get(sit) + 1);

            List<Fonctionnalite> foncts = fonctionnaliteRepository.findByProjetId(b.getProjet().getId());
            SituationProjetFicheDTO fiche = buildFicheDTO(b.getProjet(), b, foncts, annee);
            fiches.add(fiche);

            totalFonct += fiche.getTotalFonctionnalites();
            totalFonctTerminees += fiche.getFonctionnalitesTerminees();
        }

        double tauxMoyen = (totalFonct > 0) ? ((double) totalFonctTerminees / totalFonct) * 100.0 : 0.0;

        return SyntheseAnnuelleDTO.builder()
                .annee(annee)
                .totalProjets(bilans.size())
                .repartitionParSituation(repartition)
                .totalFonctionnalites(totalFonct)
                .totalFonctionnalitesTerminees(totalFonctTerminees)
                .tauxMoyenReussite(Math.round(tauxMoyen * 100.0) / 100.0)
                .fichesProjets(fiches)
                .build();
    }

    @Override
    public void supprimerBilan(Long id) {
        if (!bilanAnnuelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bilan annuel introuvable avec l'ID : " + id);
        }
        bilanAnnuelRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BilanAnnuelDTO> getProjetsEligiblesPourRapport(Integer annee) {
        return bilanAnnuelRepository.findAllByAnneeWithProjet(annee).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String genererApercuHtml(Integer annee, List<Long> projetIds) {
        List<BilanAnnuel> bilans = bilanAnnuelRepository.findAllByAnneeWithProjet(annee);

        List<BilanAnnuel> bilansSelectionnes = bilans.stream()
                .filter(b -> projetIds.contains(b.getProjet().getId()))
                .collect(Collectors.toList());

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html><head><meta charset=\"UTF-8\">\n");
        html.append("<style>\n");
        html.append("body { font-family: 'Calibri', 'Aptos', Arial, sans-serif; font-size: 11pt; line-height: 1.15; color: #333; padding: 20px; }\n");
        html.append(".projet-title { font-size: 14pt; font-weight: bold; color: #1B365D; margin-top: 24pt; margin-bottom: 6pt; border-bottom: 2px solid #1B365D; padding-bottom: 4pt; }\n");
        html.append(".section-title { font-size: 11.5pt; font-weight: bold; margin-top: 12pt; margin-bottom: 6pt; color: #1B365D; }\n");
        html.append(".description { text-align: justify; margin-bottom: 12pt; }\n");
        html.append("ul { margin: 6pt 0 6pt 24pt; padding-left: 20px; }\n");
        html.append("li { margin-bottom: 4pt; font-size: 10.5pt; }\n");
        html.append(".page-break { page-break-before: always; }\n");
        html.append(".header { text-align: center; margin-bottom: 24pt; padding-bottom: 12pt; border-bottom: 1px solid #ccc; }\n");
        html.append(".header h1 { font-size: 16pt; color: #1B365D; margin: 0; }\n");
        html.append(".header p { margin: 4pt 0; color: #666; }\n");
        html.append("</style></head><body>\n");

        html.append("<div class=\"header\">\n");
        html.append("<h1>RAPPORT ANNUEL DE SITUATION DU PORTEFEUILLE DES PROJETS</h1>\n");
        html.append("<p>Exercice ").append(annee).append("</p>\n");
        html.append("<p>Généré le ").append(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</p>\n");
        html.append("</div>\n");

        for (int i = 0; i < bilansSelectionnes.size(); i++) {
            BilanAnnuel bilan = bilansSelectionnes.get(i);
            Projet projet = bilan.getProjet();

            if (i > 0) {
                html.append("<div class=\"page-break\"></div>\n");
            }

            html.append("<div class=\"projet-title\">")
                .append(projet.getIntitule())
                .append(" <<").append(projet.getCode()).append(">></div>\n");

            html.append("<div class=\"description\">")
                .append(projet.getDescription() != null ? projet.getDescription() : "")
                .append("</div>\n");

            html.append("<div class=\"section-title\">En cours de l'année ").append(annee).append(" :</div>\n");
            html.append("<ul>\n");
            String actions = bilan.getActionsAnneeEnCours();
            if (actions != null && !actions.isBlank()) {
                for (String line : actions.split("\n")) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) {
                        html.append("<li>").append(escapeHtml(trimmed)).append("</li>\n");
                    }
                }
            } else {
                html.append("<li><em>Aucune action renseignée.</em></li>\n");
            }
            html.append("</ul>\n");

            html.append("<div class=\"section-title\">Situation du projet :</div>\n");
            html.append("<ul>\n");
            String situation = bilan.getSituationProjet() != null ? bilan.getSituationProjet().getLibelle() : "";
            html.append("<li><strong>Météo : </strong>").append(escapeHtml(situation)).append("</li>\n");
            String situationDetails = bilan.getSituationDetails();
            if (situationDetails != null && !situationDetails.isBlank()) {
                for (String line : situationDetails.split("\n")) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) {
                        html.append("<li>").append(escapeHtml(trimmed)).append("</li>\n");
                    }
                }
            }
            String fonctPrincipales = bilan.getFonctionnalitesPrincipales();
            if (fonctPrincipales != null && !fonctPrincipales.isBlank()) {
                for (String line : fonctPrincipales.split("\n")) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) {
                        html.append("<li>").append(escapeHtml(trimmed)).append("</li>\n");
                    }
                }
            }
            html.append("</ul>\n");

            html.append("<div class=\"section-title\">Perspectives :</div>\n");
            html.append("<ul>\n");
            String perspectives = bilan.getPerspectivesAnneeSuivante();
            if (perspectives != null && !perspectives.isBlank()) {
                for (String line : perspectives.split("\n")) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) {
                        html.append("<li>").append(escapeHtml(trimmed)).append("</li>\n");
                    }
                }
            } else if (bilan.getCommentaires() != null && !bilan.getCommentaires().isBlank()) {
                // Fallback sur l'ancien champ commentaires pour compatibilité
                for (String line : bilan.getCommentaires().split("\n")) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) {
                        html.append("<li>").append(escapeHtml(trimmed)).append("</li>\n");
                    }
                }
            } else {
                html.append("<li><em>Aucune perspective renseignée.</em></li>\n");
            }
            html.append("</ul>\n");
        }

        html.append("</body></html>");
        return html.toString();
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&")
                .replace("<", "<")
                .replace(">", ">")
                .replace("\"", "\"")
                .replace("'", "'");
    }

    @Override
    public byte[] genererRapportWord(Integer annee, List<Long> projetIds, String titreDocument) {
        try {
            List<BilanAnnuel> bilans = bilanAnnuelRepository.findAllByAnneeWithProjet(annee);

            List<BilanAnnuel> bilansSelectionnes = bilans.stream()
                    .filter(b -> projetIds.contains(b.getProjet().getId()))
                    .collect(Collectors.toList());

            XWPFDocument document = new XWPFDocument();

            addReportHeader(document, titreDocument, annee);

            for (int i = 0; i < bilansSelectionnes.size(); i++) {
                BilanAnnuel bilan = bilansSelectionnes.get(i);
                Projet projet = bilan.getProjet();

                if (i > 0) {
                    XWPFParagraph pageBreak = document.createParagraph();
                    pageBreak.setPageBreak(true);
                }

                addProjetToDocument(document, projet, bilan, annee);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du document Word", e);
        }
    }

    private void addReportHeader(XWPFDocument document, String titre, Integer annee) {
        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        titlePara.setSpacingAfter(120);
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(titre);
        titleRun.setBold(true);
        titleRun.setFontSize(28);
        titleRun.setColor("1B365D");
        titleRun.setFontFamily("Calibri");

        XWPFParagraph anneePara = document.createParagraph();
        anneePara.setAlignment(ParagraphAlignment.CENTER);
        anneePara.setSpacingAfter(60);
        XWPFRun anneeRun = anneePara.createRun();
        anneeRun.setText("Exercice " + annee);
        anneeRun.setFontSize(24);
        anneeRun.setColor("666666");
        anneeRun.setFontFamily("Calibri");

        XWPFParagraph datePara = document.createParagraph();
        datePara.setAlignment(ParagraphAlignment.CENTER);
        datePara.setSpacingAfter(240);
        XWPFRun dateRun = datePara.createRun();
        dateRun.setText("Généré le " + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dateRun.setFontSize(22);
        dateRun.setColor("666666");
        dateRun.setFontFamily("Calibri");

        XWPFParagraph linePara = document.createParagraph();
        linePara.setBorderBottom(Borders.SINGLE);
        linePara.setSpacingAfter(240);
    }

    private void addProjetToDocument(XWPFDocument document, Projet projet, BilanAnnuel bilan, Integer annee) {
        XWPFParagraph projetTitle = document.createParagraph();
        projetTitle.setSpacingBefore(240);
        projetTitle.setSpacingAfter(60);
        projetTitle.setBorderBottom(Borders.SINGLE);
        XWPFRun projetRun = projetTitle.createRun();
        projetRun.setText(projet.getIntitule() + " <<" + projet.getCode() + ">>");
        projetRun.setBold(true);
        projetRun.setFontSize(28);
        projetRun.setColor("1B365D");
        projetRun.setFontFamily("Calibri");

        if (projet.getDescription() != null && !projet.getDescription().isBlank()) {
            XWPFParagraph descPara = document.createParagraph();
            descPara.setAlignment(ParagraphAlignment.BOTH);
            descPara.setSpacingAfter(120);
            XWPFRun descRun = descPara.createRun();
            descRun.setText(projet.getDescription());
            descRun.setFontSize(22);
            descRun.setFontFamily("Calibri");
        }

        addSectionTitle(document, "En cours de l'année " + annee + " :");
        addBulletList(document, bilan.getActionsAnneeEnCours(), "Aucune action renseignée.");

        addSectionTitle(document, "Situation du projet :");
        List<String> situationItems = new ArrayList<>();
        if (bilan.getSituationProjet() != null) {
            situationItems.add("Météo : " + bilan.getSituationProjet().getLibelle());
        }
        String situationDetails = bilan.getSituationDetails();
        if (situationDetails != null && !situationDetails.isBlank()) {
            for (String line : situationDetails.split("\n")) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    situationItems.add(trimmed);
                }
            }
        }
        if (bilan.getFonctionnalitesPrincipales() != null && !bilan.getFonctionnalitesPrincipales().isBlank()) {
            for (String line : bilan.getFonctionnalitesPrincipales().split("\n")) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    situationItems.add(trimmed);
                }
            }
        }
        addBulletList(document, situationItems);

        addSectionTitle(document, "Perspectives :");
        String perspectives = bilan.getPerspectivesAnneeSuivante();
        if (perspectives != null && !perspectives.isBlank()) {
            addBulletListFromText(document, perspectives);
        } else if (bilan.getCommentaires() != null && !bilan.getCommentaires().isBlank()) {
            // Fallback sur l'ancien champ commentaires pour compatibilité
            addBulletListFromText(document, bilan.getCommentaires());
        } else {
            addBulletList(document, List.of("Aucune perspective renseignée."));
        }
    }

    private void addSectionTitle(XWPFDocument document, String text) {
        XWPFParagraph para = document.createParagraph();
        para.setSpacingBefore(120);
        para.setSpacingAfter(60);
        XWPFRun run = para.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(23);
        run.setColor("1B365D");
        run.setFontFamily("Calibri");
    }

    private void addBulletListFromText(XWPFDocument document, String text) {
        List<String> items = new ArrayList<>();
        for (String line : text.split("\n")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                items.add(trimmed);
            }
        }
        addBulletList(document, items);
    }

    private void addBulletList(XWPFDocument document, List<String> items) {
        for (String item : items) {
            XWPFParagraph para = document.createParagraph();
            para.setIndentationLeft(720);
            para.setSpacingAfter(40);
            XWPFRun run = para.createRun();
            run.setText("\u2022 " + item);
            run.setFontSize(21);
            run.setFontFamily("Calibri");
        }
    }

    private void addBulletList(XWPFDocument document, String text, String defaultMessage) {
        List<String> items = new ArrayList<>();
        if (text != null && !text.isBlank()) {
            for (String line : text.split("\n")) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    items.add(trimmed);
                }
            }
        }
        if (items.isEmpty()) {
            items.add(defaultMessage);
        }
        addBulletList(document, items);
    }

    @Override
    @Transactional
    public void mettreAJourSelectionRapport(Integer annee, List<Long> projetIdsInclus) {
        List<BilanAnnuel> tousBilans = bilanAnnuelRepository.findAllByAnneeWithProjet(annee);
        for (BilanAnnuel bilan : tousBilans) {
            boolean inclus = projetIdsInclus.contains(bilan.getProjet().getId());
            bilan.setInclusRapportAnnuel(inclus);
        }
        bilanAnnuelRepository.saveAll(tousBilans);
    }

    private SituationProjetFicheDTO buildFicheDTO(Projet projet, BilanAnnuel bilan, List<Fonctionnalite> fonctionnalites, Integer annee) {
        List<FonctionnaliteDTO> fonctDTOs = (fonctionnalites != null) ? fonctionnalites.stream().map(f -> FonctionnaliteDTO.builder()
                .id(f.getId())
                .projetId(f.getProjet().getId())
                .bilanAnnuelId(f.getBilanAnnuel() != null ? f.getBilanAnnuel().getId() : null)
                .code(f.getCode())
                .intitule(f.getIntitule())
                .description(f.getDescription())
                .statut(f.getStatut())
                .statutLibelle(f.getStatut().getLibelle())
                .estPrincipale(f.getEstPrincipale())
                .priorite(f.getPriorite())
                .dateCreation(f.getDateCreation())
                .dateMiseAJour(f.getDateMiseAJour())
                .build()).collect(Collectors.toList()) : Collections.emptyList();

        int total = fonctDTOs.size();
        int terminees = (int) fonctDTOs.stream().filter(f -> f.getStatut() == StatutFonctionnalite.TERMINE).count();
        int enCours = (int) fonctDTOs.stream().filter(f -> f.getStatut() == StatutFonctionnalite.EN_COURS || f.getStatut() == StatutFonctionnalite.EN_RECETTE).count();
        int bloquees = (int) fonctDTOs.stream().filter(f -> f.getStatut() == StatutFonctionnalite.BLOQUE).count();
        double avancement = total > 0 ? (double) terminees / total * 100.0 : 0.0;

        SituationProjet situation = (bilan != null && bilan.getSituationProjet() != null)
                ? bilan.getSituationProjet()
                : SituationProjet.SOUS_VIGILANCE;

        String actions = bilan != null ? bilan.getActionsAnneeEnCours() : "";
        String situationDetails = bilan != null ? bilan.getSituationDetails() : "";
        String fonctPrincipales = bilan != null ? bilan.getFonctionnalitesPrincipales() : "";
        String perspectives = bilan != null ? bilan.getPerspectivesAnneeSuivante() : "";
        String commentaires = bilan != null ? bilan.getCommentaires() : "";

        if ((fonctPrincipales == null || fonctPrincipales.isBlank()) && !fonctDTOs.isEmpty()) {
            List<String> princ = fonctDTOs.stream()
                    .filter(FonctionnaliteDTO::getEstPrincipale)
                    .map(FonctionnaliteDTO::getIntitule)
                    .collect(Collectors.toList());
            if (!princ.isEmpty()) {
                fonctPrincipales = String.join(", ", princ);
            }
        }

        Utilisateur resp = projet.getResponsable();

        return SituationProjetFicheDTO.builder()
                .projetId(projet.getId())
                .codeProjet(projet.getCode())
                .annee(annee)
                .intitule(projet.getIntitule())
                .description(projet.getDescription())
                .typeProjet(projet.getTypeProjet())
                .typeProjetLibelle(projet.getTypeProjet() != null ? projet.getTypeProjet().getLibelle() : "")
                .anneeDebut(projet.getAnneeDebut())
                .anneeFinPrevue(projet.getAnneeFinPrevue())
                .responsableId(resp != null ? resp.getId() : null)
                .responsableNomComplet(resp != null ? resp.getNomComplet() : "Non assigné")
                .responsableUsername(resp != null ? resp.getUsername() : null)
                .situationProjet(situation)
                .situationProjetLibelle(situation.getLibelle())
                .actionsAnneeEnCours(actions)
                .situationDetails(situationDetails)
                .fonctionnalitesPrincipales(fonctPrincipales)
                .perspectivesAnneeSuivante(perspectives)
                .commentaires(commentaires)
                .totalFonctionnalites(total)
                .fonctionnalitesTerminees(terminees)
                .fonctionnalitesEnCours(enCours)
                .fonctionnalitesBloquees(bloquees)
                .pourcentageAvancement(Math.round(avancement * 100.0) / 100.0)
                .listeFonctionnalites(fonctDTOs)
                .dateDerniereMiseAJour(bilan != null ? bilan.getDateMiseAJour() : projet.getDateMiseAJour())
                .build();
    }

    private BilanAnnuelDTO mapToDTO(BilanAnnuel b) {
        return BilanAnnuelDTO.builder()
                .id(b.getId())
                .projetId(b.getProjet().getId())
                .projetCode(b.getProjet().getCode())
                .projetIntitule(b.getProjet().getIntitule())
                .annee(b.getAnnee())
                .situationProjet(b.getSituationProjet())
                .situationProjetLibelle(b.getSituationProjet().getLibelle())
                .actionsAnneeEnCours(b.getActionsAnneeEnCours())
                .situationDetails(b.getSituationDetails())
                .fonctionnalitesPrincipales(b.getFonctionnalitesPrincipales())
                .perspectivesAnneeSuivante(b.getPerspectivesAnneeSuivante())
                .commentaires(b.getCommentaires())
                .dateEvaluation(b.getDateEvaluation())
                .dateMiseAJour(b.getDateMiseAJour())
                .inclusRapportAnnuel(b.getInclusRapportAnnuel())
                .build();
    }
}