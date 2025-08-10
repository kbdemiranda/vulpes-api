package io.github.vulpes.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "TipoServico", description = "Tipo de serviço de uma plataforma")
public enum TipoServico {
    STREAMING_VIDEO("Streaming de Vídeo"),
    STREAMING_MUSICA("Streaming de Música"),
    SOFTWARE("Software"),
    JOGOS("Jogos"),
    NOTICIAS("Notícias"),
    CLOUD_STORAGE("Armazenamento na Nuvem"),
    FITNESS("Fitness");

    private final String descricao;

    TipoServico(String descricao) {
        this.descricao = descricao;
    }

}
