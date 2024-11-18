package com.assignment.book.service.BooksService.Services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import com.assignment.book.service.BooksService.DTOS.BookDTO;
import com.assignment.book.service.BooksService.DTOS.TokenInformationDTO;
import com.assignment.book.service.BooksService.Entities.BookEntity;
import com.assignment.book.service.BooksService.Exceptions.BookExistsException;
import com.assignment.book.service.BooksService.Exceptions.BookNotFoundException;
import com.assignment.book.service.BooksService.Repositories.BookRepository;
import com.assignment.order.service.OrderService.exceptions.UnauthorizedUserException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TokenValidationService tokenValidationService;

    public List<BookDTO> getAllBooks() {
	    List<BookEntity> bookEntities = bookRepository.findAll(); 
	    return bookEntities.stream() 
	        .map(bookEntity -> modelMapper.map(bookEntity, BookDTO.class))
	        .collect(Collectors.toList()); 
    }

    public BookDTO getBookById(Long id) {
         BookEntity bookEntity = getBookEntityById(id);
		return modelMapper.map(bookEntity, BookDTO.class);
    }

    
    public BookDTO addBook(BookEntity book,HttpServletRequest request) {
    	getTokenInformation(request);
    	Optional<BookEntity> existingBook = bookRepository.findByTitle(book.getTitle());
        if (existingBook.isPresent()) {
        	throw new BookExistsException("Book with title '" + book.getTitle() + "' already exists.");
        }
        BookEntity bookEntity =  bookRepository.save(book);
        return modelMapper.map(bookEntity, BookDTO.class);
    }

    
    public void updateStock(Long id, int stock,HttpServletRequest request) {
    	getTokenInformation(request);
        BookEntity book = getBookEntityById(id);
        book.setStock(stock);
        bookRepository.save(book);
    }

    
    public void deleteBook(Long id,HttpServletRequest request) {
    	getTokenInformation(request);
    	getBookEntityById(id);
        bookRepository.deleteById(id);
    }
    
    private BookEntity getBookEntityById(Long id) {
    	return bookRepository.findById(id)
          		 .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
    }
    
    private void getTokenInformation(HttpServletRequest request) {	
    	
    	if(request.getHeader("X-INTERNAL")!=null) {
    		System.out.println("Internal services call");
    	}
    	else {
    		try {
    			tokenValidationService.retrieveUserDetailsFromToken(request.getHeader("Authorization"));  
    			System.out.println("User is admin");
    			} catch (RuntimeException e) {
    				System.out.println("User is not admin");
    				throw new UnauthorizedUserException("User is not allowed to perform this action");
    			}
    	}
	}
}

