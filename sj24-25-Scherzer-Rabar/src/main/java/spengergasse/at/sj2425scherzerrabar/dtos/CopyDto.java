package spengergasse.at.sj2425scherzerrabar.dtos;

import spengergasse.at.sj2425scherzerrabar.domain.*;

public record CopyDto(String apiKey, String publisherApiKey, String bookType, Integer pageCount, String bookApiKey, String branchApiKey) {

    public CopyDto(String apiKey, String publisherApiKey, BookType bookType, Integer pageCount, String bookApiKey, String branchApiKey) {
        this(apiKey, publisherApiKey, bookType != null ? bookType.name() : null, pageCount, bookApiKey, branchApiKey);
    }

    public static CopyDto copyDtoFromCopy(Copy copy) {
        return new CopyDto(copy.getCopyApiKey().apiKey(), copy.getPublisher().getPublisherApiKey().apiKey(), copy.getBookType().name(), copy.getPageCount(), copy.getBook().getBookApiKey().apiKey(),copy.getInBranch().getBranchApiKey().apiKey());
    }
}
