package site.soulware.cocina360.internalcontrol.domain.model.command;

import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FormatFieldDraft;

import java.util.List;
import java.util.UUID;

public record ReplaceFormatFieldsCommand(UUID formatId, List<FormatFieldDraft> fields) {}
