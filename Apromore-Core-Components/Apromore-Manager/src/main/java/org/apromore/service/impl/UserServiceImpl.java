/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.impl;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.dao.SearchHistoryRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.SearchHistory;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the UserService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final Integer MIN_SEARCH_SAVE = 0;
    private static final Integer MAX_SEARCH_SAVE = 10;

    private UserRepository userRepo;
    private SearchHistoryRepository searchHistoryRepo;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param userRepository User Repository.
     */
    @Inject
    public UserServiceImpl(final UserRepository userRepository, final SearchHistoryRepository searchHistoryRepository) {
        userRepo = userRepository;
        searchHistoryRepo = searchHistoryRepository;
    }



    /**
     * @see org.apromore.service.UserService#findAllUsers()
     * {@inheritDoc}
     * <p/>
     * NOTE: This might need to convert (or allow for) to the models used in the webservices.
     */
    @Override
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    /**
     * @see org.apromore.service.UserService#findUserByLogin(String)
     * {@inheritDoc}
     */
    @Override
    public User findUserByLogin(String username) throws UserNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user != null) {
            return user;
        } else {
            throw new UserNotFoundException("User with username (" + username + ") could not be found.");
        }
    }

    /**
     * @see org.apromore.service.UserService#writeUser(org.apromore.dao.model.User)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void writeUser(User user) {
        User dbUser = userRepo.findOne(user.getId());
        dbUser.setSearchHistories(user.getSearchHistories());
        userRepo.save(dbUser);
    }

    /**
     * @see org.apromore.service.UserService#updateUserSearchHistory(org.apromore.dao.model.User, java.util.List)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void updateUserSearchHistory(User user, List<SearchHistory> searchHistories) {
        List<SearchHistory> history = new ArrayList<>();
        User dbUser = userRepo.findByUsername(user.getUsername());

        if (searchHistories != null) {
            Set<String> existingSearchTerms = new HashSet<>();
            for (int position = 0; history.size() < 10 && position < searchHistories.size(); position++) {
                 SearchHistory searchHistory = searchHistories.get(position);
                 if (!existingSearchTerms.contains(searchHistory.getSearch())) {
                     searchHistory.setIndex(history.size());
                     searchHistory.setUser(dbUser);
                     history.add(searchHistory);
                     existingSearchTerms.add(searchHistory.getSearch());
                 }
            }
        }
        user.setSearchHistories(history);

        // Delete existing search history
        List<SearchHistory> existingSearchHistory = searchHistoryRepo.findByUserOrderByIndexDesc(dbUser);
        searchHistoryRepo.deleteInBatch(existingSearchHistory);

        searchHistoryRepo.save(history);
        dbUser.setSearchHistories(history);
        userRepo.save(dbUser);
    }
}