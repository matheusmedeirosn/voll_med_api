package med.voll.api.domain.consulta.validacoes;

import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import med.voll.api.domain.consulta.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidarConflitoHorarioMedico implements ValidadorAgendamentoConsultas {

    @Autowired
    private ConsultaRepository repository;

    public void validar(DadosAgendamentoConsulta dados){
        var conflitoHorarioMedico = repository.existsByMedicoIdAndData(dados.idMedico(), dados.data());
        if(conflitoHorarioMedico){
            throw new ValidacaoException("Médico já possui outra consulta agendada para este horário");
        }
    }

}
